package com.tianshang.periodpal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.data.model.CyclePrediction
import com.tianshang.periodpal.data.model.CycleStatistics
import com.tianshang.periodpal.utils.PredictionEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AnalysisViewModel(
    private val application: PeriodPalApplication
) : ViewModel() {
    
    private val periodRepository = application.database.periodRecordDao()
    private val symptomRepository = application.database.dailySymptomDao()
    private val predictionEngine = PredictionEngine()
    
    val statistics: StateFlow<CycleStatistics?> = combine(
        periodRepository.getAllRecords(),
        symptomRepository.getAllSymptoms()
    ) { records, symptoms ->
        predictionEngine.calculateStatistics(records, symptoms)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    
    val predictions: StateFlow<List<CyclePrediction>> = periodRepository.getAllRecords()
        .map { records ->
            predictionEngine.predictNextCycles(records, emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AnalysisViewModel(context.applicationContext as PeriodPalApplication) as T
        }
    }
}
