package com.tianshang.periodpal.data.local

import androidx.room.*
import com.tianshang.periodpal.data.model.BmiRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface BmiRecordDao {
    @Query("SELECT * FROM bmi_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<BmiRecord>>
    
    @Query("SELECT * FROM bmi_records WHERE date = :date")
    suspend fun getRecordForDate(date: LocalDate): BmiRecord?
    
    @Query("SELECT * FROM bmi_records ORDER BY date DESC LIMIT 1")
    suspend fun getLatestRecord(): BmiRecord?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BmiRecord): Long
    
    @Update
    suspend fun update(record: BmiRecord)
    
    @Delete
    suspend fun delete(record: BmiRecord)
    
    @Query("SELECT AVG(bmi) FROM bmi_records WHERE date >= :startDate")
    suspend fun getAverageBmiSince(startDate: LocalDate): Float?
    
    @Query("SELECT * FROM bmi_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getRecordsInRange(startDate: LocalDate, endDate: LocalDate): List<BmiRecord>
}
