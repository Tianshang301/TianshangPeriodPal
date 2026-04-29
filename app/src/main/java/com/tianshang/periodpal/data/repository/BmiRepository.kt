package com.tianshang.periodpal.data.repository

import com.tianshang.periodpal.data.local.BmiRecordDao
import com.tianshang.periodpal.data.model.BmiRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class BmiRepository(private val bmiRecordDao: BmiRecordDao) {
    val allRecords: Flow<List<BmiRecord>> = bmiRecordDao.getAllRecords()
    
    suspend fun getRecordForDate(date: LocalDate): BmiRecord? {
        return bmiRecordDao.getRecordForDate(date)
    }
    
    suspend fun getLatestRecord(): BmiRecord? {
        return bmiRecordDao.getLatestRecord()
    }
    
    suspend fun insert(record: BmiRecord): Long {
        return bmiRecordDao.insert(record)
    }
    
    suspend fun update(record: BmiRecord) {
        bmiRecordDao.update(record)
    }
    
    suspend fun delete(record: BmiRecord) {
        bmiRecordDao.delete(record)
    }
    
    suspend fun getAverageBmiSince(startDate: LocalDate): Float? {
        return bmiRecordDao.getAverageBmiSince(startDate)
    }
    
    suspend fun getRecordsInRange(startDate: LocalDate, endDate: LocalDate): List<BmiRecord> {
        return bmiRecordDao.getRecordsInRange(startDate, endDate)
    }
    
    fun calculateBmi(heightCm: Float, weightKg: Float): Float {
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }
    
    fun getBmiCategory(bmi: Float): String {
        return when {
            bmi < 18.5f -> "偏瘦"
            bmi < 24f -> "正常"
            bmi < 28f -> "偏胖"
            else -> "肥胖"
        }
    }
}
