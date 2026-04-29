package com.tianshang.periodpal.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.utils.BackupManager
import com.tianshang.periodpal.utils.ExportManager
import kotlinx.coroutines.launch

class BackupViewModel(
    private val application: PeriodPalApplication
) : ViewModel() {
    
    private val backupManager = BackupManager(application)
    private val exportManager = ExportManager(application)
    
    fun exportDatabase(callback: (Uri?) -> Unit) {
        viewModelScope.launch {
            val uri = backupManager.exportDatabase()
            callback(uri)
        }
    }
    
    fun importDatabase(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            // This would need to be triggered by a file picker
            callback(false)
        }
    }
    
    fun exportToCSV(callback: (Uri?) -> Unit) {
        viewModelScope.launch {
            val records = application.database.periodRecordDao().getAllRecordsSync()
            val symptoms = application.database.dailySymptomDao().getSymptomsInRange(
                java.time.LocalDate.MIN,
                java.time.LocalDate.MAX
            )
            val uri = exportManager.exportToCSV(records, symptoms)
            callback(uri)
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BackupViewModel(context.applicationContext as PeriodPalApplication) as T
        }
    }
}
