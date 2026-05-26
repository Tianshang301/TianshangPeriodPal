package com.tianshang.periodpal.data.local

import androidx.room.*
import com.tianshang.periodpal.data.model.PeriodRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PeriodRecordDao {
    @Query("SELECT * FROM period_records WHERE isDeleted = 0 ORDER BY startDate DESC")
    fun getAllRecords(): Flow<List<PeriodRecord>>
    
    @Query("SELECT * FROM period_records WHERE isDeleted = 0 AND startDate <= :date AND (endDate IS NULL OR endDate >= :date)")
    suspend fun getRecordForDate(date: LocalDate): PeriodRecord?
    
    @Query("SELECT * FROM period_records WHERE isDeleted = 0 ORDER BY startDate DESC LIMIT 1")
    suspend fun getLastRecord(): PeriodRecord?
    
    @Query("SELECT * FROM period_records WHERE isDeleted = 0 ORDER BY startDate ASC")
    suspend fun getAllRecordsSync(): List<PeriodRecord>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PeriodRecord): Long
    
    @Update
    suspend fun update(record: PeriodRecord)
    
    @Query("UPDATE period_records SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT * FROM period_records WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedRecords(): Flow<List<PeriodRecord>>
    
    @Query("UPDATE period_records SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restoreRecord(id: Long)
    
    @Query("DELETE FROM period_records WHERE isDeleted = 1 AND deletedAt < :timestamp")
    suspend fun permanentlyDeleteOldRecords(timestamp: Long)
}
