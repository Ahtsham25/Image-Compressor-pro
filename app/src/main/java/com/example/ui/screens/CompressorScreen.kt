package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AdConfig
import com.example.model.AppLanguage
import com.example.model.CompressionFormat
import com.example.model.CompressionPreset
import com.example.model.CompressionResult
import com.example.model.ImageDetails
import com.example.ui.components.AdBannerCard
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.components.SideBySideComparison
import com.example.ui.components.StatComparisonCard
import com.example.util.AppStrings
import com.example.util.FileUtils

@Composable
fun CompressorScreen(
    uiState: com.example.viewmodel.CompressionUiState,
    selectedPreset: CompressionPreset,
    selectedFormat: CompressionFormat,
    customQuality: Int,
    customScale: Int,
    language: AppLanguage,
    adConfig: AdConfig,
    onImageSelected: (Uri) -> Unit,
    onPresetChanged: (CompressionPreset) -> Unit,
    onFormatChanged: (CompressionFormat) -> Unit,
    onCustomQualityChanged: (Int) -> Unit,
    onCustomScaleChanged: (Int) -> Unit,
    onStartCompression: () -> Unit,
    onSaveToGallery: () -> Unit,
    onShareImage: () -> Unit,
    onReset: () -> Unit,
    isDeveloperMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App top hero banner if in Idle or Inspecting state
        if (uiState is com.example.viewmodel.CompressionUiState.Idle) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(Color(0xFF06B6D4).copy(alpha = 0.5f), Color(0xFF8B5CF6).copy(alpha = 0.5f))
                        ),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_compressor_hero_1790304643031),
                    contentDescription = "Compressor Hero",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xDD0B0F19))
                            )
                        ),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (language == AppLanguage.URDU) "امیج کمپریسر پرو" else "Image Compressor Pro",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (language == AppLanguage.URDU)
                                "تین طاقتور موڈز: سٹینڈرڈ، ہائی اور لو کمپریشن"
                            else
                                "3 Modes: Standard, High & Low Compression with instant stats",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Select Photo Button Card
            SelectPhotoCard(
                language = language,
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Information overview of the 3 presets
            PresetOverviewCard(language = language)
        }

        // When image is selected or compressing or success
        when (uiState) {
            is com.example.viewmodel.CompressionUiState.Inspecting -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF06B6D4))
                }
            }

            is com.example.viewmodel.CompressionUiState.ImageSelected -> {
                ImagePreviewCard(
                    details = uiState.details,
                    language = language,
                    onChangeImage = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3 Modes Selection Section
                CompressionPresetSelector(
                    selectedPreset = selectedPreset,
                    language = language,
                    onPresetSelected = onPresetChanged
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Custom sliders if CUSTOM preset is selected
                AnimatedVisibility(visible = selectedPreset == CompressionPreset.CUSTOM) {
                    CustomParametersCard(
                        customQuality = customQuality,
                        customScale = customScale,
                        language = language,
                        onQualityChange = onCustomQualityChanged,
                        onScaleChange = onCustomScaleChanged
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Format Selector (JPEG, WebP, PNG)
                FormatSelector(
                    selectedFormat = selectedFormat,
                    onFormatSelected = onFormatChanged
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Compress Button
                CompressActionButton(
                    language = language,
                    preset = selectedPreset,
                    onClick = onStartCompression
                )
            }

            is com.example.viewmodel.CompressionUiState.Compressing -> {
                CompressingProgressCard(
                    details = uiState.details,
                    language = language
                )
            }

            is com.example.viewmodel.CompressionUiState.Success -> {
                val result = uiState.result
                var previewMode by remember { mutableStateOf("side_by_side") }

                // Stat Comparison Card with real size comparison and savings %
                StatComparisonCard(
                    result = result,
                    language = language
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Preview Mode Toggle Selector (Side-by-Side vs Split Slider)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111827), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { previewMode = "side_by_side" }
                            .testTag("preview_mode_side_by_side"),
                        color = if (previewMode == "side_by_side") Color(0xFF0E7490) else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.URDU) "ساتھ ساتھ موازنہ (Side-by-Side)" else "Side-by-Side Preview",
                            color = if (previewMode == "side_by_side") Color.White else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { previewMode = "slider" }
                            .testTag("preview_mode_slider"),
                        color = if (previewMode == "slider") Color(0xFF0E7490) else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.URDU) "اسپلٹ سلائیڈر (Wipe Slider)" else "Split Wipe Slider",
                            color = if (previewMode == "slider") Color.White else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (previewMode == "side_by_side") {
                    SideBySideComparison(
                        result = result,
                        language = language
                    )
                } else {
                    BeforeAfterSlider(
                        originalModel = result.originalDetails.uri,
                        compressedModel = result.compressedFile,
                        originalLabel = if (language == AppLanguage.URDU) "اصل تصویر" else "Original",
                        compressedLabel = if (language == AppLanguage.URDU) "کمپریسڈ" else "Compressed"
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons: Save to Gallery & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSaveToGallery,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("save_gallery_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.savedToGallery) Color(0xFF10B981) else Color(0xFF06B6D4)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.savedToGallery) Icons.Default.Check else Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.savedToGallery)
                                (if (language == AppLanguage.URDU) "محفوظ شدہ!" else "Saved!")
                            else
                                AppStrings.saveToGallery(language),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onShareImage,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("share_image_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF38BDF8)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.shareImage(language),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Choose Another Image Button
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("compress_another_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.selectAnotherImage(language), fontSize = 13.sp)
                }
            }

            is com.example.viewmodel.CompressionUiState.Error -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF7F1D1D).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "⚠️ ${uiState.message}", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onReset) {
                            Text("Try Again")
                        }
                    }
                }
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Ad Banner Card at bottom
        AdBannerCard(
            adConfig = adConfig,
            showDevInfo = isDeveloperMode
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SelectPhotoCard(
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("select_photo_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFF06B6D4), Color(0xFF0E7490))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = AppStrings.selectImagePrompt(language),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (language == AppLanguage.URDU)
                    "کسی بھی تصویر کا سائز کم کریں بغیر کوالٹی خراب کیے"
                else
                    "Tap to choose any image from your gallery to reduce file size",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFF06B6D4).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "JPEG • PNG • WebP",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PresetOverviewCard(language: AppLanguage) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF111827),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = AppStrings.compressionModesTitle(language),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            PresetInfoRow(
                icon = Icons.Default.AutoAwesome,
                color = Color(0xFF06B6D4),
                title = if (language == AppLanguage.URDU) "1. سٹینڈرڈ کمپریشن (Standard)" else "1. Standard Compression",
                description = if (language == AppLanguage.URDU)
                    "60% سے 75% تک سائز میں کمی، بہترین کوالٹی کے ساتھ"
                else
                    "~60-75% reduction, balanced clarity & size"
            )

            Spacer(modifier = Modifier.height(10.dp))

            PresetInfoRow(
                icon = Icons.Default.Bolt,
                color = Color(0xFFEF4444),
                title = if (language == AppLanguage.URDU) "2. ہائی کمپریشن (High)" else "2. High Compression",
                description = if (language == AppLanguage.URDU)
                    "80% سے 92% تک سائز میں زبردست بچت، سب سے چھوٹی فائل"
                else
                    "~80-92% reduction, smallest file size for fast uploads"
            )

            Spacer(modifier = Modifier.height(10.dp))

            PresetInfoRow(
                icon = Icons.Default.HighQuality,
                color = Color(0xFF10B981),
                title = if (language == AppLanguage.URDU) "3. لو کمپریشن (Low)" else "3. Low Compression",
                description = if (language == AppLanguage.URDU)
                    "بہترین کوالٹی اور تفصیلات برقرار، ہلکی کمپریشن"
                else
                    "Original high-res retained, maximum visual fidelity"
            )
        }
    }
}

@Composable
private fun PresetInfoRow(
    icon: ImageVector,
    color: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ImagePreviewCard(
    details: ImageDetails,
    language: AppLanguage,
    onChangeImage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("image_preview_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = details.uri,
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = details.fileName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = FileUtils.formatBytes(details.sizeBytes),
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${details.width} × ${details.height}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onChangeImage,
                modifier = Modifier.testTag("change_image_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Change Image",
                    tint = Color(0xFF06B6D4)
                )
            }
        }
    }
}

@Composable
private fun CompressionPresetSelector(
    selectedPreset: CompressionPreset,
    language: AppLanguage,
    onPresetSelected: (CompressionPreset) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = AppStrings.compressionModesTitle(language),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3 Cards + Custom button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetOptionCard(
                preset = CompressionPreset.LOW,
                title = if (language == AppLanguage.URDU) "لو" else "Low",
                badge = "~30%",
                isSelected = selectedPreset == CompressionPreset.LOW,
                accentColor = Color(0xFF10B981),
                onClick = { onPresetSelected(CompressionPreset.LOW) },
                modifier = Modifier.weight(1f)
            )

            PresetOptionCard(
                preset = CompressionPreset.STANDARD,
                title = if (language == AppLanguage.URDU) "سٹینڈرڈ" else "Standard",
                badge = "~70%",
                isSelected = selectedPreset == CompressionPreset.STANDARD,
                accentColor = Color(0xFF06B6D4),
                onClick = { onPresetSelected(CompressionPreset.STANDARD) },
                modifier = Modifier.weight(1f)
            )

            PresetOptionCard(
                preset = CompressionPreset.HIGH,
                title = if (language == AppLanguage.URDU) "ہائی" else "High",
                badge = "~90%",
                isSelected = selectedPreset == CompressionPreset.HIGH,
                accentColor = Color(0xFFF43F5E),
                onClick = { onPresetSelected(CompressionPreset.HIGH) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Custom chip
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPresetSelected(CompressionPreset.CUSTOM) }
                .testTag("custom_preset_tile"),
            color = if (selectedPreset == CompressionPreset.CUSTOM) Color(0xFF1E293B) else Color(0xFF111827),
            shape = RoundedCornerShape(12.dp),
            border = if (selectedPreset == CompressionPreset.CUSTOM)
                CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4))))
            else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = if (selectedPreset == CompressionPreset.CUSTOM) Color(0xFF8B5CF6) else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.URDU) "کسٹم موڈ (معیار اور اسکیل خود منتخب کریں)" else "Custom Mode (Fine-tune quality & scale)",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (selectedPreset == CompressionPreset.CUSTOM) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetOptionCard(
    preset: CompressionPreset,
    title: String,
    badge: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color(0xFF1E293B),
        label = "BorderColor"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .testTag("preset_${preset.name.lowercase()}"),
        color = if (isSelected) accentColor.copy(alpha = 0.15f) else Color(0xFF111827),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CustomParametersCard(
    customQuality: Int,
    customScale: Int,
    language: AppLanguage,
    onQualityChange: (Int) -> Unit,
    onScaleChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag("custom_parameters_card"),
        color = Color(0xFF131C2E),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Quality Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (language == AppLanguage.URDU) "کوالٹی (معیار)" else "Quality Level",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$customQuality%",
                    color = Color(0xFF06B6D4),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = customQuality.toFloat(),
                onValueChange = { onQualityChange(it.toInt()) },
                valueRange = 10f..100f,
                steps = 18,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF06B6D4),
                    activeTrackColor = Color(0xFF06B6D4),
                    inactiveTrackColor = Color(0xFF334155)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Scale Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (language == AppLanguage.URDU) "ڈائمینشن اسکیل (سائز)" else "Resolution Scale",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$customScale%",
                    color = Color(0xFF8B5CF6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = customScale.toFloat(),
                onValueChange = { onScaleChange(it.toInt()) },
                valueRange = 20f..100f,
                steps = 16,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF8B5CF6),
                    activeTrackColor = Color(0xFF8B5CF6),
                    inactiveTrackColor = Color(0xFF334155)
                )
            )
        }
    }
}

@Composable
private fun FormatSelector(
    selectedFormat: CompressionFormat,
    onFormatSelected: (CompressionFormat) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompressionFormat.values().forEach { format ->
            FilterChip(
                selected = selectedFormat == format,
                onClick = { onFormatSelected(format) },
                label = { Text(format.displayName, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF0E7490),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF1E293B),
                    labelColor = Color(0xFF94A3B8)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CompressActionButton(
    language: AppLanguage,
    preset: CompressionPreset,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .scale(pulseScale)
            .testTag("compress_action_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = AppStrings.compressButton(language),
            color = Color.Black,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun CompressingProgressCard(
    details: ImageDetails,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .testTag("compressing_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp),
                color = Color(0xFF06B6D4),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = AppStrings.compressing(language),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (language == AppLanguage.URDU)
                    "پکسلز کو ری سکیل اور آپٹمائز کیا جا رہا ہے..."
                else
                    "Optimizing pixels and downscaling buffer...",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
            )
        }
    }
}
