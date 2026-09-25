package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compression_records")
data class CompressionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val reductionPercentage: Int,
    val originalWidth: Int,
    val originalHeight: Int,
    val compressedWidth: Int,
    val compressedHeight: Int,
    val compressionPreset: String,
    val format: String,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis()
)
