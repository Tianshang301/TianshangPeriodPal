package com.tianshang.periodpal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.data.model.CyclePrediction
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord
import com.tianshang.periodpal.utils.PredictionEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class CalendarViewModel(
    private val application: PeriodPalApplication
) : ViewModel() {
    
    private val periodRepository = application.database.periodRecordDao()
    private val symptomRepository = application.database.dailySymptomDao()
    private val predictionEngine = PredictionEngine()
    
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    val records: StateFlow<List<PeriodRecord>> = periodRepository.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val symptoms: StateFlow<List<DailySymptom>> = symptomRepository.getAllSymptoms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val predictions: StateFlow<List<CyclePrediction>> = combine(records, symptoms) { records, symptoms ->
        predictionEngine.predictNextCycles(records, symptoms)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val currentCycleDay: StateFlow<Int> = records.map { records ->
        val lastRecord = records.firstOrNull { !it.isDeleted }
        if (lastRecord != null && lastRecord.endDate == null) {
            val days = ChronoUnit.DAYS.between(lastRecord.startDate, LocalDate.now()).toInt()
            if (days >= 0) days + 1 else 0
        } else {
            0
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    
    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }
    
    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }
    
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalendarViewModel(context.applicationContext as PeriodPalApplication) as T
        }
    }
}
