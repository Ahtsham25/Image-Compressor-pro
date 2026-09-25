package com.example

import com.example.model.CompressionFormat
import com.example.model.CompressionPreset
import com.example.model.CompressionResult
import com.example.model.ImageDetails
import com.example.util.FileUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun testFormatBytes() {
        assertEquals("0 B", FileUtils.formatBytes(0))
        assertEquals("500 B", FileUtils.formatBytes(500))
        assertEquals("1 KB", FileUtils.formatBytes(1024))
        assertEquals("1.5 MB", FileUtils.formatBytes((1.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun testCompressionPresets() {
        assertEquals(3, CompressionPreset.values().count { it != CompressionPreset.CUSTOM })
        assertTrue(CompressionPreset.LOW.defaultQuality > CompressionPreset.STANDARD.defaultQuality)
        assertTrue(CompressionPreset.STANDARD.defaultQuality > CompressionPreset.HIGH.defaultQuality)
    }

    @Test
    fun testSavingsCalculation() {
        val details = ImageDetails(
            uri = android.net.Uri.parse("content://media/external/images/media/1"),
            fileName = "sample.jpg",
            sizeBytes = 1000000L,
            width = 4000,
            height = 3000,
            mimeType = "image/jpeg"
        )
        val dummyFile = File("test.jpg")
        val result = CompressionResult(
            originalDetails = details,
            compressedFile = dummyFile,
            compressedSizeBytes = 200000L,
            compressedWidth = 2000,
            compressedHeight = 1500,
            preset = CompressionPreset.STANDARD,
            format = CompressionFormat.JPEG,
            quality = 75,
            compressionDurationMs = 120
        )

        assertEquals(800000L, result.savingsBytes)
        assertEquals(80, result.savingsPercentage)
    }
}
