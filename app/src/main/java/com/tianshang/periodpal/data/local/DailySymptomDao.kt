package com.tianshang.periodpal.data.local

import androidx.room.*
import com.tianshang.periodpal.data.model.DailySymptom
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailySymptomDao {
    @Query("SELECT * FROM daily_symptoms WHERE date = :date")
    suspend fun getSymptomForDate(date: LocalDate): DailySymptom?
    
    @Query("SELECT * FROM daily_symptoms WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getSymptomsInRange(startDate: LocalDate, endDate: LocalDate): List<DailySymptom>
    
    @Query("SELECT * FROM daily_symptoms ORDER BY date DESC")
    fun getAllSymptoms(): Flow<List<DailySymptom>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(symptom: DailySymptom)
    
    @Update
    suspend fun update(symptom: DailySymptom)
    
    @Delete
    suspend fun delete(symptom: DailySymptom)
}
