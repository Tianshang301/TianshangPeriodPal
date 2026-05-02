package com.tianshang.periodpal.data.repository

import com.tianshang.periodpal.data.local.DailySymptomDao
import com.tianshang.periodpal.data.model.DailySymptom
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class SymptomRepository(private val dailySymptomDao: DailySymptomDao) {
    val allSymptoms: Flow<List<DailySymptom>> = dailySymptomDao.getAllSymptoms()
    
    suspend fun getSymptomForDate(date: LocalDate): DailySymptom? {
        return dailySymptomDao.getSymptomForDate(date)
    }
    
    suspend fun getSymptomsInRange(startDate: LocalDate, endDate: LocalDate): List<DailySymptom> {
        return dailySymptomDao.getSymptomsInRange(startDate, endDate)
    }
    
    suspend fun insert(symptom: DailySymptom) {
        dailySymptomDao.insert(symptom)
    }
    
    suspend fun update(symptom: DailySymptom) {
        dailySymptomDao.update(symptom)
    }
    
    suspend fun delete(symptom: DailySymptom) {
        dailySymptomDao.delete(symptom)
    }
}
