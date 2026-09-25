package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File

@Composable
fun BeforeAfterSlider(
    originalModel: Any,
    compressedModel: Any,
    originalLabel: String = "Original",
    compressedLabel: String = "Compressed",
    modifier: Modifier = Modifier
) {
    var sliderFraction by remember { mutableFloatStateOf(0.5f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0B0F19))
            .testTag("before_after_slider_container")
    ) {
        val widthPx = constraints.maxWidth.toFloat()

        // Background: Compressed Image (Full width)
        AsyncImage(
            model = compressedModel,
            contentDescription = "Compressed Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay: Original Image clipped to sliderFraction width
        val density = LocalDensity.current
        val clipWidthDp = with(density) { (widthPx * sliderFraction).toDp() }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(clipWidthDp)
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        ) {
            AsyncImage(
                model = originalModel,
                contentDescription = "Original Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(density) { widthPx.toDp() })
            )
        }

        // Divider Line
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .offset { IntOffset((widthPx * sliderFraction).toInt() - 1, 0) }
                .background(Color.White)
        )

        // Draggable Handle
        Box(
            modifier = Modifier
                .size(44.dp)
                .offset {
                    IntOffset(
                        (widthPx * sliderFraction - with(density) { 22.dp.toPx() }).toInt(),
                        (constraints.maxHeight / 2 - with(density) { 22.dp.toPx() }).toInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newFraction = sliderFraction + (dragAmount.x / widthPx)
                        sliderFraction = newFraction.coerceIn(0.05f, 0.95f)
                    }
                }
                .background(Color(0xFF06B6D4), CircleShape)
                .testTag("slider_handle"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Drag to compare",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        // Labels
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = originalLabel,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
            color = Color(0xFF10B981).copy(alpha = 0.85f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = compressedLabel,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
