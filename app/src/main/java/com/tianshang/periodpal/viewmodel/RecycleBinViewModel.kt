package com.tianshang.periodpal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.data.model.PeriodRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecycleBinViewModel(
    private val application: PeriodPalApplication
) : ViewModel() {
    
    private val periodRepository = application.database.periodRecordDao()
    
    val deletedRecords: StateFlow<List<PeriodRecord>> = periodRepository.getDeletedRecords()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun restoreRecord(id: Long) {
        viewModelScope.launch {
            periodRepository.restoreRecord(id)
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecycleBinViewModel(context.applicationContext as PeriodPalApplication) as T
        }
    }
}
