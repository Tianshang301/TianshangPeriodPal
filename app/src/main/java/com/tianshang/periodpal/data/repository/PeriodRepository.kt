package com.tianshang.periodpal.data.repository

import com.tianshang.periodpal.data.local.PeriodRecordDao
import com.tianshang.periodpal.data.model.PeriodRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class PeriodRepository(private val periodRecordDao: PeriodRecordDao) {
    val allRecords: Flow<List<PeriodRecord>> = periodRecordDao.getAllRecords()
    val deletedRecords: Flow<List<PeriodRecord>> = periodRecordDao.getDeletedRecords()
    
    suspend fun getRecordForDate(date: LocalDate): PeriodRecord? {
        return periodRecordDao.getRecordForDate(date)
    }
    
    suspend fun getLastRecord(): PeriodRecord? {
        return periodRecordDao.getLastRecord()
    }
    
    suspend fun getAllRecordsSync(): List<PeriodRecord> {
        return periodRecordDao.getAllRecordsSync()
    }
    
    suspend fun insert(record: PeriodRecord): Long {
        return periodRecordDao.insert(record)
    }
    
    suspend fun update(record: PeriodRecord) {
        periodRecordDao.update(record)
    }
    
    suspend fun softDelete(id: Long) {
        periodRecordDao.softDelete(id)
    }
    
    suspend fun restoreRecord(id: Long) {
        periodRecordDao.restoreRecord(id)
    }
    
    suspend fun permanentlyDeleteOldRecords() {
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)
        periodRecordDao.permanentlyDeleteOldRecords(thirtyDaysAgo)
    }
}
