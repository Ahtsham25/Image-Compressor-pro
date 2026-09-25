package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppLanguage
import com.example.ui.components.AdSettingsDialog
import com.example.ui.components.AppLogo
import com.example.ui.components.ContactInfoDialog
import com.example.ui.components.DeveloperPinDialog
import com.example.ui.components.InterstitialAdDialog
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.TermsDialog
import com.example.ui.components.openWhatsApp
import com.example.ui.screens.CompressorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppStrings
import com.example.viewmodel.CompressorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppRoot(viewModel: CompressorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedPreset by viewModel.selectedPreset.collectAsStateWithLifecycle()
    val selectedFormat by viewModel.selectedFormat.collectAsStateWithLifecycle()
    val customQuality by viewModel.customQuality.collectAsStateWithLifecycle()
    val customScale by viewModel.customScale.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val adConfig by viewModel.adConfig.collectAsStateWithLifecycle()
    val showInterstitialAd by viewModel.showInterstitialAd.collectAsStateWithLifecycle()
    val historyRecords by viewModel.historyRecords.collectAsStateWithLifecycle()
    val totalBytesSaved by viewModel.totalBytesSaved.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    var showSplash by rememberSaveable { mutableStateOf(true) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var isDeveloperMode by rememberSaveable { mutableStateOf(false) }
    var showDevPinDialog by remember { mutableStateOf(false) }
    var showAdSettingsDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var topBarTapCount by remember { mutableIntStateOf(0) }

    if (showSplash) {
        SplashScreen(
            language = language,
            onSplashFinished = { showSplash = false }
        )
        return
    }

    // Legal & Contact Dialogs
    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
    }
    if (showTermsDialog) {
        TermsDialog(onDismiss = { showTermsDialog = false })
    }
    if (showContactDialog) {
        ContactInfoDialog(onDismiss = { showContactDialog = false })
    }

    // Developer PIN Dialog
    if (showDevPinDialog) {
        DeveloperPinDialog(
            language = language,
            onSuccess = {
                showDevPinDialog = false
                isDeveloperMode = true
                showAdSettingsDialog = true
                Toast.makeText(context, "Developer Mode Activated!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showDevPinDialog = false }
        )
    }

    // Interstitial Ad Overlay
    if (showInterstitialAd) {
        InterstitialAdDialog(
            adConfig = adConfig,
            showDevInfo = isDeveloperMode,
            onDismiss = { viewModel.dismissInterstitialAd() }
        )
    }

    // Ad Settings Dialog (Developer only)
    if (showAdSettingsDialog) {
        AdSettingsDialog(
            adConfig = adConfig,
            language = language,
            onSave = { updated -> viewModel.updateAdConfig(updated) },
            onTestInterstitial = { viewModel.triggerInterstitialAd() },
            onDismiss = { showAdSettingsDialog = false }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Color(0xFF0B0F19),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isDeveloperMode) {
                                topBarTapCount += 1
                                if (topBarTapCount >= 5) {
                                    topBarTapCount = 0
                                    showDevPinDialog = true
                                }
                            }
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppLogo(size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.appTitle(language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        if (isDeveloperMode) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "DEV",
                                    color = Color(0xFF34D399),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Ad Settings Button (ONLY visible if Developer Mode is unlocked!)
                    if (isDeveloperMode) {
                        IconButton(
                            onClick = { showAdSettingsDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("top_bar_ad_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Developer Ad Settings",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // 3-Dot More Menu
                    Box {
                        IconButton(
                            onClick = { showMoreMenu = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("top_bar_more_menu_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            modifier = Modifier.background(Color(0xFF1E293B))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Privacy Policy", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    showMoreMenu = false
                                    showPrivacyDialog = true
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Terms & Conditions", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    showMoreMenu = false
                                    showTermsDialog = true
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("WhatsApp Channel", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    showMoreMenu = false
                                    openWhatsApp(context)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Contact: +923087179003", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    showMoreMenu = false
                                    showContactDialog = true
                                }
                            )

                            if (!isDeveloperMode) {
                                HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 4.dp))
                                DropdownMenuItem(
                                    text = { Text("Developer Access", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        showDevPinDialog = true
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0B0F19)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF111827),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = AppStrings.compressorTab(language)
                        )
                    },
                    label = { Text(AppStrings.compressorTab(language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color(0xFF06B6D4),
                        indicatorColor = Color(0xFF06B6D4),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_compressor")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (historyRecords.isNotEmpty()) {
                                    Badge(containerColor = Color(0xFF10B981)) {
                                        Text("${historyRecords.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = AppStrings.historyTab(language)
                            )
                        }
                    },
                    label = { Text(AppStrings.historyTab(language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color(0xFF06B6D4),
                        indicatorColor = Color(0xFF06B6D4),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_history")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = AppStrings.settingsTab(language)
                        )
                    },
                    label = { Text(AppStrings.settingsTab(language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color(0xFF06B6D4),
                        indicatorColor = Color(0xFF06B6D4),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = selectedTab,
            label = "ScreenCrossfade",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> CompressorScreen(
                    uiState = uiState,
                    selectedPreset = selectedPreset,
                    selectedFormat = selectedFormat,
                    customQuality = customQuality,
                    customScale = customScale,
                    language = language,
                    adConfig = adConfig,
                    onImageSelected = { uri -> viewModel.onImageSelected(uri) },
                    onPresetChanged = { preset -> viewModel.setPreset(preset) },
                    onFormatChanged = { format -> viewModel.setFormat(format) },
                    onCustomQualityChanged = { q -> viewModel.setCustomQuality(q) },
                    onCustomScaleChanged = { s -> viewModel.setCustomScale(s) },
                    onStartCompression = { viewModel.startCompression() },
                    onSaveToGallery = { viewModel.saveCompressedToGallery() },
                    onShareImage = { viewModel.shareCompressedImage() },
                    onReset = { viewModel.resetCompression() },
                    isDeveloperMode = isDeveloperMode
                )

                1 -> HistoryScreen(
                    records = historyRecords,
                    totalBytesSaved = totalBytesSaved ?: 0L,
                    language = language,
                    onShareRecord = { record -> viewModel.shareHistoryItem(record) },
                    onDeleteRecord = { record -> viewModel.deleteHistoryRecord(record) },
                    onClearAll = { viewModel.clearAllHistory() }
                )

                2 -> SettingsScreen(
                    adConfig = adConfig,
                    language = language,
                    isDeveloperMode = isDeveloperMode,
                    onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                    onOpenAdSettings = { showAdSettingsDialog = true },
                    onTestInterstitial = { viewModel.triggerInterstitialAd() },
                    onDeveloperModeChanged = { enabled -> isDeveloperMode = enabled }
                )
            }
        }
    }
}
