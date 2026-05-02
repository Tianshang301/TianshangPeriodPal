package com.tianshang.periodpal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.service.ReminderScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class RecordViewModel(
    private val application: PeriodPalApplication
) : ViewModel() {
    
    private val periodRepository = application.database.periodRecordDao()
    private val symptomRepository = application.database.dailySymptomDao()
    
    private fun rescheduleReminders() {
        viewModelScope.launch {
            try {
                val records = periodRepository.getAllRecordsSync()
                val symptoms = symptomRepository.getAllSymptoms().first()
                val settings = SettingsRepository(application).settings.first()
                ReminderScheduler.scheduleReminders(application, records, symptoms, settings)
            } catch (_: Exception) {}
        }
    }
    
    fun quickStartPeriod(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val record = PeriodRecord(
                startDate = date,
                endDate = null
            )
            periodRepository.insert(record)
            rescheduleReminders()
        }
    }
    
    fun quickEndPeriod(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val lastRecord = periodRepository.getLastRecord()
            if (lastRecord != null && lastRecord.endDate == null) {
                val updatedRecord = lastRecord.copy(endDate = date)
                periodRepository.update(updatedRecord)
            }
            rescheduleReminders()
        }
    }
    
    fun saveRecord(
        date: LocalDate,
        flowLevel: Int?,
        painLevel: Int?,
        symptoms: List<String>,
        sexualActivity: Boolean,
        ovulationTest: String?,
        cervicalMucus: String?,
        bodyTemperature: Float?,
        notes: String?
    ) {
        viewModelScope.launch {
            val existingSymptom = symptomRepository.getSymptomForDate(date)
            val symptom = DailySymptom(
                date = date,
                symptoms = symptoms.joinToString(",", "[", "]") { "\"$it\"" },
                sexualActivity = sexualActivity,
                ovulationTestResult = ovulationTest,
                cervicalMucus = cervicalMucus,
                bodyTemperature = bodyTemperature,
                notes = notes
            )
            
            if (existingSymptom != null) {
                symptomRepository.update(symptom)
            } else {
                symptomRepository.insert(symptom)
            }
            
            if (flowLevel != null || painLevel != null) {
                val existingRecord = periodRepository.getRecordForDate(date)
                if (existingRecord != null) {
                    val updatedRecord = existingRecord.copy(
                        flowLevel = flowLevel ?: existingRecord.flowLevel,
                        painLevel = painLevel ?: existingRecord.painLevel,
                        notes = notes ?: existingRecord.notes
                    )
                    periodRepository.update(updatedRecord)
                }
            }
            
            rescheduleReminders()
        }
    }
    
    fun deleteRecord(date: LocalDate) {
        viewModelScope.launch {
            val periodRecord = periodRepository.getRecordForDate(date)
            periodRecord?.let {
                periodRepository.softDelete(it.id)
            }
            
            val symptom = symptomRepository.getSymptomForDate(date)
            symptom?.let {
                symptomRepository.delete(it)
            }
            
            rescheduleReminders()
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecordViewModel(context.applicationContext as PeriodPalApplication) as T
        }
    }
}
