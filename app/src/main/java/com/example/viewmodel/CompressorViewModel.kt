package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CompressionRecord
import com.example.model.AdConfig
import com.example.model.AppLanguage
import com.example.model.CompressionFormat
import com.example.model.CompressionPreset
import com.example.model.CompressionResult
import com.example.model.ImageDetails
import com.example.util.AdManager
import com.example.util.FileUtils
import com.example.util.ImageCompressorEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed interface CompressionUiState {
    data object Idle : CompressionUiState
    data object Inspecting : CompressionUiState
    data class ImageSelected(val details: ImageDetails) : CompressionUiState
    data class Compressing(val details: ImageDetails, val progress: Float) : CompressionUiState
    data class Success(val result: CompressionResult, val savedToGallery: Boolean = false) : CompressionUiState
    data class Error(val message: String) : CompressionUiState
}

class CompressorViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = ImageCompressorEngine(application.applicationContext)
    private val database = AppDatabase.getDatabase(application.applicationContext)
    private val dao = database.compressionDao()

    private val _uiState = MutableStateFlow<CompressionUiState>(CompressionUiState.Idle)
    val uiState: StateFlow<CompressionUiState> = _uiState.asStateFlow()

    private val _selectedPreset = MutableStateFlow(CompressionPreset.STANDARD)
    val selectedPreset: StateFlow<CompressionPreset> = _selectedPreset.asStateFlow()

    private val _selectedFormat = MutableStateFlow(CompressionFormat.JPEG)
    val selectedFormat: StateFlow<CompressionFormat> = _selectedFormat.asStateFlow()

    private val _customQuality = MutableStateFlow(80)
    val customQuality: StateFlow<Int> = _customQuality.asStateFlow()

    private val _customScale = MutableStateFlow(100)
    val customScale: StateFlow<Int> = _customScale.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _adConfig = MutableStateFlow(AdConfig())
    val adConfig: StateFlow<AdConfig> = _adConfig.asStateFlow()

    private val _showInterstitialAd = MutableStateFlow(false)
    val showInterstitialAd: StateFlow<Boolean> = _showInterstitialAd.asStateFlow()

    private val _showRewardedAd = MutableStateFlow(false)
    val showRewardedAd: StateFlow<Boolean> = _showRewardedAd.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val historyRecords = dao.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBytesSaved = dao.getTotalBytesSaved()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val recordCount = dao.getRecordCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Load persistent custom Google IDs - permanently free of test IDs
        val app = application.applicationContext
        _adConfig.value = AdConfig(
            bannerAdId = AdManager.getCustomBannerId(app),
            interstitialAdId = AdManager.getCustomInterstitialId(app),
            rewardedAdId = AdManager.getCustomRewardedId(app),
            appId = AdManager.getCustomAppId(app),
            adsEnabled = AdManager.isAdsEnabled(app)
        )
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = CompressionUiState.Inspecting
            val details = engine.getImageDetails(uri)
            if (details != null) {
                _uiState.value = CompressionUiState.ImageSelected(details)
            } else {
                _uiState.value = CompressionUiState.Error("Failed to inspect the selected image.")
            }
        }
    }

    fun setPreset(preset: CompressionPreset) {
        _selectedPreset.value = preset
    }

    fun setFormat(format: CompressionFormat) {
        _selectedFormat.value = format
    }

    fun setCustomQuality(quality: Int) {
        _customQuality.value = quality.coerceIn(5, 100)
    }

    fun setCustomScale(scale: Int) {
        _customScale.value = scale.coerceIn(10, 100)
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.URDU) AppLanguage.ENGLISH else AppLanguage.URDU
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun updateAdConfig(newConfig: AdConfig) {
        val app = getApplication<Application>().applicationContext
        _adConfig.value = newConfig
        AdManager.setAdsEnabled(app, newConfig.adsEnabled)
        AdManager.setCustomBannerId(app, newConfig.bannerAdId)
        AdManager.setCustomInterstitialId(app, newConfig.interstitialAdId)
        AdManager.setCustomRewardedId(app, newConfig.rewardedAdId)
        AdManager.setCustomAppId(app, newConfig.appId)
    }

    private var pendingResetAfterInterstitial = false

    fun dismissInterstitialAd() {
        _showInterstitialAd.value = false
        if (pendingResetAfterInterstitial) {
            pendingResetAfterInterstitial = false
            resetCompression()
        }
    }

    fun triggerInterstitialAd() {
        if (_adConfig.value.adsEnabled && _adConfig.value.interstitialAdId.isNotBlank()) {
            _showInterstitialAd.value = true
        }
    }

    fun requestResetWithInterstitial() {
        if (_adConfig.value.adsEnabled && _adConfig.value.interstitialAdId.isNotBlank()) {
            pendingResetAfterInterstitial = true
            _showInterstitialAd.value = true
        } else {
            resetCompression()
        }
    }

    fun triggerRewardedAd() {
        if (_adConfig.value.adsEnabled && _adConfig.value.rewardedAdId.isNotBlank()) {
            _showRewardedAd.value = true
        }
    }

    fun dismissRewardedAd() {
        _showRewardedAd.value = false
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun startCompression() {
        val current = _uiState.value
        val details = when (current) {
            is CompressionUiState.ImageSelected -> current.details
            is CompressionUiState.Success -> current.result.originalDetails
            else -> return
        }

        viewModelScope.launch {
            _uiState.value = CompressionUiState.Compressing(details, 0.35f)
            try {
                val result = engine.compressImage(
                    details = details,
                    preset = _selectedPreset.value,
                    format = _selectedFormat.value,
                    customQuality = _customQuality.value,
                    customScalePercent = _customScale.value
                )

                val record = CompressionRecord(
                    fileName = details.fileName,
                    originalSizeBytes = details.sizeBytes,
                    compressedSizeBytes = result.compressedSizeBytes,
                    originalWidth = details.width,
                    originalHeight = details.height,
                    compressedWidth = result.compressedWidth,
                    compressedHeight = result.compressedHeight,
                    format = result.format.name,
                    compressionPreset = result.preset.name,
                    reductionPercentage = result.savingsPercentage,
                    filePath = result.compressedFile.absolutePath
                )
                dao.insertRecord(record)

                _uiState.value = CompressionUiState.Success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = CompressionUiState.Error(e.message ?: "Unknown compression error")
            }
        }
    }

    /**
     * Saves the compressed image directly to the device's public photo gallery using MediaStore,
     * including an immediate success toast notification.
     */
    fun saveCompressedToGallery() {
        val current = _uiState.value
        if (current !is CompressionUiState.Success) return

        viewModelScope.launch(Dispatchers.IO) {
            val file = current.result.compressedFile
            val savedUri = FileUtils.saveToGallery(
                getApplication(),
                file,
                current.result.format.name
            )
            withContext(Dispatchers.Main) {
                if (savedUri != null) {
                    _uiState.value = current.copy(savedToGallery = true)
                    _toastMessage.value = if (_language.value == AppLanguage.URDU)
                        "تصویر کامیابی سے گیلری میں محفوظ ہو گئی!"
                    else
                        "Image saved to Gallery successfully! (Pictures/ImageCompressor)"
                } else {
                    _toastMessage.value = if (_language.value == AppLanguage.URDU)
                        "تصویر گیلری میں محفوظ کرنے میں ناکامی ہوئی۔"
                    else
                        "Failed to save image to gallery."
                }
            }
        }
    }

    /**
     * Saves a previously compressed image from history directly to the device's public photo gallery using MediaStore,
     * including an immediate success toast notification.
     */
    fun saveHistoryRecordToGallery(record: CompressionRecord) {
        val file = File(record.filePath)
        if (!file.exists()) {
            _toastMessage.value = if (_language.value == AppLanguage.URDU)
                "فائل موجود نہیں ہے۔"
            else
                "File no longer exists."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val savedUri = FileUtils.saveToGallery(
                getApplication(),
                file,
                record.format
            )
            withContext(Dispatchers.Main) {
                if (savedUri != null) {
                    _toastMessage.value = if (_language.value == AppLanguage.URDU)
                        "تصویر کامیابی سے گیلری میں محفوظ ہو گئی!"
                    else
                        "Image saved to Gallery successfully! (Pictures/ImageCompressor)"
                } else {
                    _toastMessage.value = if (_language.value == AppLanguage.URDU)
                        "تصویر گیلری میں محفوظ کرنے میں ناکامی ہوئی۔"
                    else
                        "Failed to save image to gallery."
                }
            }
        }
    }

    fun shareCompressedImage() {
        val current = _uiState.value
        if (current !is CompressionUiState.Success) return
        FileUtils.shareImage(
            getApplication(),
            current.result.compressedFile,
            current.result.format.mimeType
        )
    }

    fun shareHistoryItem(record: CompressionRecord) {
        val file = File(record.filePath)
        if (file.exists()) {
            val mimeType = when (record.format.uppercase()) {
                "PNG" -> "image/png"
                "WEBP" -> "image/webp"
                else -> "image/jpeg"
            }
            FileUtils.shareImage(getApplication(), file, mimeType)
        } else {
            _toastMessage.value = "File no longer exists in cache."
        }
    }

    fun deleteHistoryRecord(record: CompressionRecord) {
        viewModelScope.launch {
            dao.deleteRecord(record)
            val file = File(record.filePath)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }

    fun resetCompression() {
        _uiState.value = CompressionUiState.Idle
    }
}
