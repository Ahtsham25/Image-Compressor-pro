package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import com.example.model.CompressionFormat
import com.example.model.CompressionPreset
import com.example.model.CompressionResult
import com.example.model.ImageDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

class ImageCompressorEngine(private val context: Context) {

    private val cacheDir: File
        get() = File(context.cacheDir, "compressed").apply { if (!exists()) mkdirs() }

    suspend fun getImageDetails(uri: Uri): ImageDetails? = withContext(Dispatchers.IO) {
        try {
            var fileName = "image_${System.currentTimeMillis()}"
            var fileSize: Long = 0
            val contentResolver = context.contentResolver

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }

            if (fileSize <= 0) {
                contentResolver.openInputStream(uri)?.use { stream ->
                    fileSize = stream.available().toLong()
                }
            }

            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            val mimeType = options.outMimeType ?: contentResolver.getType(uri) ?: "image/jpeg"
            val width = options.outWidth
            val height = options.outHeight

            ImageDetails(
                uri = uri,
                fileName = fileName,
                sizeBytes = fileSize,
                width = width,
                height = height,
                mimeType = mimeType
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun compressImage(
        details: ImageDetails,
        preset: CompressionPreset,
        format: CompressionFormat = CompressionFormat.JPEG,
        customQuality: Int = 80,
        customScalePercent: Int = 100
    ): CompressionResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val quality = when (preset) {
            CompressionPreset.CUSTOM -> customQuality.coerceIn(5, 100)
            else -> preset.defaultQuality
        }

        // Calculate target max dimensions
        val (targetWidth, targetHeight) = calculateTargetDimensions(
            origWidth = details.width,
            origHeight = details.height,
            preset = preset,
            customScale = customScalePercent
        )

        // Load bitmap with optimal inSampleSize
        val sampleSize = calculateInSampleSize(details.width, details.height, targetWidth, targetHeight)
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        var decodedBitmap = context.contentResolver.openInputStream(details.uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        } ?: throw IllegalStateException("Failed to decode image")

        // Fix EXIF orientation
        decodedBitmap = fixOrientation(details.uri, decodedBitmap)

        // Scale bitmap to exact target dimensions if necessary
        val finalBitmap = if (decodedBitmap.width != targetWidth || decodedBitmap.height != targetHeight) {
            val scaled = Bitmap.createScaledBitmap(decodedBitmap, targetWidth, targetHeight, true)
            if (scaled != decodedBitmap) {
                decodedBitmap.recycle()
            }
            scaled
        } else {
            decodedBitmap
        }

        val compressFormat = getCompressFormat(format)
        val fileExtension = format.extension
        val outputFile = File(cacheDir, "comp_${System.currentTimeMillis()}.$fileExtension")

        FileOutputStream(outputFile).use { outStream ->
            finalBitmap.compress(compressFormat, quality, outStream)
        }

        val duration = System.currentTimeMillis() - startTime
        val compressedLength = outputFile.length()

        val finalWidth = finalBitmap.width
        val finalHeight = finalBitmap.height

        finalBitmap.recycle()

        CompressionResult(
            originalDetails = details,
            compressedFile = outputFile,
            compressedSizeBytes = compressedLength,
            compressedWidth = finalWidth,
            compressedHeight = finalHeight,
            preset = preset,
            format = format,
            quality = quality,
            compressionDurationMs = duration
        )
    }

    private fun calculateTargetDimensions(
        origWidth: Int,
        origHeight: Int,
        preset: CompressionPreset,
        customScale: Int
    ): Pair<Int, Int> {
        if (origWidth <= 0 || origHeight <= 0) return Pair(1080, 1080)

        return when (preset) {
            CompressionPreset.LOW -> {
                // Keep dimensions intact or cap at 3840
                scaleToMaxDimension(origWidth, origHeight, preset.maxDimension)
            }
            CompressionPreset.STANDARD -> {
                // Cap at 2048
                scaleToMaxDimension(origWidth, origHeight, preset.maxDimension)
            }
            CompressionPreset.HIGH -> {
                // Cap at 1280
                scaleToMaxDimension(origWidth, origHeight, preset.maxDimension)
            }
            CompressionPreset.CUSTOM -> {
                val scaleFactor = (customScale.coerceIn(10, 100).toFloat() / 100f)
                val w = (origWidth * scaleFactor).roundToInt().coerceAtLeast(100)
                val h = (origHeight * scaleFactor).roundToInt().coerceAtLeast(100)
                Pair(w, h)
            }
        }
    }

    private fun scaleToMaxDimension(width: Int, height: Int, maxDim: Int): Pair<Int, Int> {
        val largest = max(width, height)
        if (largest <= maxDim) return Pair(width, height)

        val ratio = maxDim.toFloat() / largest.toFloat()
        val targetW = (width * ratio).roundToInt().coerceAtLeast(1)
        val targetH = (height * ratio).roundToInt().coerceAtLeast(1)
        return Pair(targetW, targetH)
    }

    private fun calculateInSampleSize(w: Int, h: Int, reqW: Int, reqH: Int): Int {
        var inSampleSize = 1
        if (h > reqH || w > reqW) {
            val halfHeight = h / 2
            val halfWidth = w / 2
            while ((halfHeight / inSampleSize) >= reqH && (halfWidth / inSampleSize) >= reqW) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun fixOrientation(uri: Uri, bitmap: Bitmap): Bitmap {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                    else -> return bitmap
                }
                val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                if (rotated != bitmap) {
                    bitmap.recycle()
                }
                return rotated
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try { inputStream?.close() } catch (_: Exception) {}
        }
        return bitmap
    }

    @Suppress("DEPRECATION")
    private fun getCompressFormat(format: CompressionFormat): Bitmap.CompressFormat {
        return when (format) {
            CompressionFormat.PNG -> Bitmap.CompressFormat.PNG
            CompressionFormat.WEBP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    Bitmap.CompressFormat.WEBP
                }
            }
            CompressionFormat.JPEG -> Bitmap.CompressFormat.JPEG
        }
    }
}
