package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompressionDao {
    @Query("SELECT * FROM compression_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<CompressionRecord>>

    @Query("SELECT COUNT(*) FROM compression_records")
    fun getRecordCount(): Flow<Int>

    @Query("SELECT SUM(originalSizeBytes - compressedSizeBytes) FROM compression_records")
    fun getTotalBytesSaved(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CompressionRecord): Long

    @Delete
    suspend fun deleteRecord(record: CompressionRecord)

    @Query("DELETE FROM compression_records")
    suspend fun clearAll()
}
