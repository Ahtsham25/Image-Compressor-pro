package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DecimalFormat

object FileUtils {

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
        val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
        val df = DecimalFormat("#,##0.#")
        return "${df.format(value)} ${units[digitGroups]}"
    }

    fun saveToGallery(context: Context, file: File, formatName: String): Uri? {
        val extension = when (formatName.uppercase()) {
            "PNG" -> "png"
            "WEBP" -> "webp"
            else -> "jpg"
        }
        val mimeType = when (formatName.uppercase()) {
            "PNG" -> "image/png"
            "WEBP" -> "image/webp"
            else -> "image/jpeg"
        }
        val displayName = "Compressed_${System.currentTimeMillis()}.$extension"

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ImageCompressor")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val collectionUri = MediaStore.Images.Media.getContentUri(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            } else {
                MediaStore.VOLUME_EXTERNAL
            }
        )

        val imageUri = resolver.insert(collectionUri, contentValues) ?: return null

        try {
            resolver.openOutputStream(imageUri)?.use { out ->
                FileInputStream(file).use { inStream ->
                    inStream.copyTo(out)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
            return imageUri
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun shareImage(context: Context, file: File, mimeType: String = "image/jpeg") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Share Compressed Image")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
