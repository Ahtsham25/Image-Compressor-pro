package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.AppLanguage
import com.example.model.CompressionResult
import com.example.util.FileUtils

enum class ComparisonLayout {
    HORIZONTAL_SIDE_BY_SIDE, // Side-by-side columns
    VERTICAL_STACKED         // Top-and-bottom stacked
}

@Composable
fun SideBySideComparison(
    result: CompressionResult,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var comparisonLayout by remember { mutableStateOf(ComparisonLayout.HORIZONTAL_SIDE_BY_SIDE) }
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var isFullscreen by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("side_by_side_comparison_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(Color(0xFF06B6D4).copy(alpha = 0.4f), Color(0xFF10B981).copy(alpha = 0.4f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Control Toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF06B6D4).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = Color(0xFF06B6D4),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (language == AppLanguage.URDU) "کوالٹی کا ساتھ ساتھ موازنہ" else "Quality Comparison",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Layout Toggle (Columns vs Rows)
                    IconButton(
                        onClick = {
                            comparisonLayout = if (comparisonLayout == ComparisonLayout.HORIZONTAL_SIDE_BY_SIDE) {
                                ComparisonLayout.VERTICAL_STACKED
                            } else {
                                ComparisonLayout.HORIZONTAL_SIDE_BY_SIDE
                            }
                        },
                        modifier = Modifier.size(32.dp).testTag("toggle_comparison_layout_btn")
                    ) {
                        Icon(
                            imageVector = if (comparisonLayout == ComparisonLayout.HORIZONTAL_SIDE_BY_SIDE)
                                Icons.Default.ViewAgenda
                            else
                                Icons.Default.ViewColumn,
                            contentDescription = "Toggle Layout",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Fullscreen Button
                    IconButton(
                        onClick = { isFullscreen = true },
                        modifier = Modifier.size(32.dp).testTag("open_fullscreen_comparison_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Zoom Selector Chips (1x, 2x, 4x pixel peep)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.URDU) "زوم:" else "Zoom:",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                listOf(1f to "1x Normal", 2f to "2x Zoom", 4f to "4x Pixel Peep").forEach { (scale, label) ->
                    FilterChip(
                        selected = zoomScale == scale,
                        onClick = {
                            zoomScale = scale
                            if (scale == 1f) panOffset = Offset.Zero
                        },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0E7490),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }

                if (zoomScale > 1f || panOffset != Offset.Zero) {
                    IconButton(
                        onClick = {
                            zoomScale = 1f
                            panOffset = Offset.Zero
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Zoom",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dual Side by Side Panes
            DualComparisonView(
                originalUri = result.originalDetails.uri,
                originalSizeText = FileUtils.formatBytes(result.originalDetails.sizeBytes),
                originalDimText = "${result.originalDetails.width}×${result.originalDetails.height}",
                compressedFile = result.compressedFile,
                compressedSizeText = FileUtils.formatBytes(result.compressedSizeBytes),
                compressedDimText = "${result.compressedWidth}×${result.compressedHeight}",
                savingsText = "-${result.savingsPercentage}%",
                layout = comparisonLayout,
                zoomScale = zoomScale,
                panOffset = panOffset,
                onTransform = { panDelta ->
                    panOffset += panDelta
                },
                language = language,
                height = 260.dp
            )
        }
    }

    // Fullscreen Dialog Modal
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF050814))
                    .padding(12.dp)
                    .testTag("fullscreen_comparison_dialog")
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == AppLanguage.URDU) "فل اسکرین کوالٹی انسپکشن" else "Side-by-Side Quality Inspection",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    comparisonLayout = if (comparisonLayout == ComparisonLayout.HORIZONTAL_SIDE_BY_SIDE)
                                        ComparisonLayout.VERTICAL_STACKED
                                    else
                                        ComparisonLayout.HORIZONTAL_SIDE_BY_SIDE
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (comparisonLayout == ComparisonLayout.HORIZONTAL_SIDE_BY_SIDE)
                                        Icons.Default.ViewAgenda
                                    else
                                        Icons.Default.ViewColumn,
                                    contentDescription = "Switch Layout",
                                    tint = Color(0xFF38BDF8)
                                )
                            }

                            IconButton(
                                onClick = { isFullscreen = false },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Zoom bar in fullscreen
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(1f to "1x", 2f to "2x", 4f to "4x", 8f to "8x").forEach { (scale, label) ->
                            FilterChip(
                                selected = zoomScale == scale,
                                onClick = {
                                    zoomScale = scale
                                    if (scale == 1f) panOffset = Offset.Zero
                                },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0E7490),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                        DualComparisonView(
                            originalUri = result.originalDetails.uri,
                            originalSizeText = FileUtils.formatBytes(result.originalDetails.sizeBytes),
                            originalDimText = "${result.originalDetails.width}×${result.originalDetails.height}",
                            compressedFile = result.compressedFile,
                            compressedSizeText = FileUtils.formatBytes(result.compressedSizeBytes),
                            compressedDimText = "${result.compressedWidth}×${result.compressedHeight}",
                            savingsText = "-${result.savingsPercentage}%",
                            layout = comparisonLayout,
                            zoomScale = zoomScale,
                            panOffset = panOffset,
                            onTransform = { panDelta ->
                                panOffset += panDelta
                            },
                            language = language,
                            height = null // fills available space
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DualComparisonView(
    originalUri: Any,
    originalSizeText: String,
    originalDimText: String,
    compressedFile: Any,
    compressedSizeText: String,
    compressedDimText: String,
    savingsText: String,
    layout: ComparisonLayout,
    zoomScale: Float,
    panOffset: Offset,
    onTransform: (Offset) -> Unit,
    language: AppLanguage,
    height: androidx.compose.ui.unit.Dp?
) {
    val containerModifier = if (height != null) {
        Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0B0F19))
            .pointerInput(zoomScale) {
                detectTransformGestures { _, pan, _, _ ->
                    if (zoomScale > 1f) {
                        onTransform(pan)
                    }
                }
            }
    } else {
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0B0F19))
            .pointerInput(zoomScale) {
                detectTransformGestures { _, pan, _, _ ->
                    if (zoomScale > 1f) {
                        onTransform(pan)
                    }
                }
            }
    }

    if (layout == ComparisonLayout.HORIZONTAL_SIDE_BY_SIDE) {
        Row(
            modifier = containerModifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Original Image Pane
            SingleImageInspectionPane(
                model = originalUri,
                title = if (language == AppLanguage.URDU) "اصل تصویر" else "Original",
                sizeText = originalSizeText,
                dimText = originalDimText,
                badgeColor = Color(0xFFEF4444),
                badgeText = "100%",
                zoomScale = zoomScale,
                panOffset = panOffset,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            // Divider Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF334155))
            )

            // Compressed Image Pane
            SingleImageInspectionPane(
                model = compressedFile,
                title = if (language == AppLanguage.URDU) "کمپریسڈ" else "Compressed",
                sizeText = compressedSizeText,
                dimText = compressedDimText,
                badgeColor = Color(0xFF10B981),
                badgeText = savingsText,
                zoomScale = zoomScale,
                panOffset = panOffset,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    } else {
        Column(
            modifier = containerModifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Original Image Pane (Top)
            SingleImageInspectionPane(
                model = originalUri,
                title = if (language == AppLanguage.URDU) "اصل تصویر" else "Original",
                sizeText = originalSizeText,
                dimText = originalDimText,
                badgeColor = Color(0xFFEF4444),
                badgeText = "100%",
                zoomScale = zoomScale,
                panOffset = panOffset,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )

            // Divider Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(0xFF334155))
            )

            // Compressed Image Pane (Bottom)
            SingleImageInspectionPane(
                model = compressedFile,
                title = if (language == AppLanguage.URDU) "کمپریسڈ" else "Compressed",
                sizeText = compressedSizeText,
                dimText = compressedDimText,
                badgeColor = Color(0xFF10B981),
                badgeText = savingsText,
                zoomScale = zoomScale,
                panOffset = panOffset,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SingleImageInspectionPane(
    model: Any,
    title: String,
    sizeText: String,
    dimText: String,
    badgeColor: Color,
    badgeText: String,
    zoomScale: Float,
    panOffset: Offset,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
    ) {
        // Image with zoom & pan transforms
        AsyncImage(
            model = model,
            contentDescription = title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                    translationX = panOffset.x
                    translationY = panOffset.y
                }
        )

        // Overlay Header Info Bar
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Overlay Footer Stats Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(
                    text = sizeText,
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dimText,
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp
                )
            }
        }
    }
}
