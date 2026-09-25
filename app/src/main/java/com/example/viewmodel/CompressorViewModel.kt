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
import com.example.util.FileUtils
import com.example.util.ImageCompressorEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val historyRecords = dao.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBytesSaved = dao.getTotalBytesSaved()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val recordCount = dao.getRecordCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

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
        _adConfig.value = newConfig
    }

    fun dismissInterstitialAd() {
        _showInterstitialAd.value = false
    }

    fun triggerInterstitialAd() {
        if (_adConfig.value.adsEnabled) {
            _showInterstitialAd.value = true
        }
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

                // Save record in Room database
                val record = CompressionRecord(
                    fileName = details.fileName,
                    originalSizeBytes = details.sizeBytes,
                    compressedSizeBytes = result.compressedSizeBytes,
                    reductionPercentage = result.savingsPercentage,
                    originalWidth = details.width,
                    originalHeight = details.height,
                    compressedWidth = result.compressedWidth,
                    compressedHeight = result.compressedHeight,
                    compressionPreset = result.preset.name,
                    format = result.format.name,
                    filePath = result.compressedFile.absolutePath
                )
                dao.insertRecord(record)

                _uiState.value = CompressionUiState.Success(result)

                // Trigger interstitial ad after compression if ads enabled
                if (_adConfig.value.adsEnabled) {
                    _showInterstitialAd.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = CompressionUiState.Error(e.message ?: "Unknown compression error")
            }
        }
    }

    fun saveCompressedToGallery() {
        val current = _uiState.value
        if (current !is CompressionUiState.Success) return

        viewModelScope.launch {
            val file = current.result.compressedFile
            val savedUri = FileUtils.saveToGallery(
                getApplication(),
                file,
                current.result.format.name
            )
            if (savedUri != null) {
                _uiState.value = current.copy(savedToGallery = true)
                _toastMessage.value = if (_language.value == AppLanguage.URDU)
                    "تصویر کامیابی سے گیلری میں محفوظ ہو گئی!"
                else
                    "Saved to Gallery (Pictures/ImageCompressor)"
            } else {
                _toastMessage.value = "Failed to save image to gallery."
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
