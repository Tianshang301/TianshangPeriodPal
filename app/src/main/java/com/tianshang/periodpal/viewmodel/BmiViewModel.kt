package com.tianshang.periodpal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.data.model.BmiRecord
import com.tianshang.periodpal.data.repository.BmiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class BmiViewModel(
    private val application: PeriodPalApplication
) : ViewModel() {
    
    private val bmiRepository = BmiRepository(application.database.bmiRecordDao())
    
    val allRecords: StateFlow<List<BmiRecord>> = bmiRepository.allRecords
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val latestRecord: StateFlow<BmiRecord?> = bmiRepository.allRecords
        .map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    
    fun addRecord(height: Float, weight: Float, notes: String? = null) {
        viewModelScope.launch {
            val bmi = bmiRepository.calculateBmi(height, weight)
            val record = BmiRecord(
                date = LocalDate.now(),
                height = height,
                weight = weight,
                bmi = bmi,
                notes = notes
            )
            bmiRepository.insert(record)
        }
    }
    
    fun deleteRecord(record: BmiRecord) {
        viewModelScope.launch {
            bmiRepository.delete(record)
        }
    }
    
    fun getBmiCategory(bmi: Float): Int {
        return bmiRepository.getBmiCategory(bmi)
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BmiViewModel(context.applicationContext as PeriodPalApplication) as T
        }
    }
}
