package com.example.model

import android.net.Uri
import java.io.File

data class ImageDetails(
    val uri: Uri,
    val fileName: String,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val mimeType: String
)

data class CompressionResult(
    val originalDetails: ImageDetails,
    val compressedFile: File,
    val compressedSizeBytes: Long,
    val compressedWidth: Int,
    val compressedHeight: Int,
    val preset: CompressionPreset,
    val format: CompressionFormat,
    val quality: Int,
    val compressionDurationMs: Long
) {
    val savingsBytes: Long
        get() = (originalDetails.sizeBytes - compressedSizeBytes).coerceAtLeast(0)

    val savingsPercentage: Int
        get() = if (originalDetails.sizeBytes > 0) {
            val ratio = (savingsBytes.toDouble() / originalDetails.sizeBytes.toDouble()) * 100.0
            ratio.toInt().coerceIn(0, 99)
        } else 0
}

data class AdConfig(
    val bannerAdId: String = "ca-app-pub-3940256099942544/6300978111",
    val interstitialAdId: String = "ca-app-pub-3940256099942544/1033173712",
    val rewardedAdId: String = "ca-app-pub-3940256099942544/5224354917",
    val appId: String = "ca-app-pub-3940256099942544~3347511713",
    val adsEnabled: Boolean = true,
    val testMode: Boolean = true
)

enum class AppLanguage {
    ENGLISH,
    URDU
}
