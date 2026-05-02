package com.tianshang.periodpal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.data.repository.UserSettings
import com.tianshang.periodpal.service.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val application: PeriodPalApplication,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<UserSettings> = settingsRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        UserSettings()
    )
    
    private fun updateAndReschedule(settings: UserSettings) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settings)
            rescheduleReminders(settings)
        }
    }
    
    private suspend fun rescheduleReminders(currentSettings: UserSettings) {
        val records = application.database.periodRecordDao().getAllRecordsSync()
        val symptoms = application.database.dailySymptomDao().getAllSymptoms().first()
        ReminderScheduler.scheduleReminders(application, records, symptoms, currentSettings)
    }
    
    fun updatePeriodReminderEnabled(enabled: Boolean) {
        updateAndReschedule(settings.value.copy(periodReminderEnabled = enabled))
    }
    
    fun updatePeriodReminderDays(days: Int) {
        updateAndReschedule(settings.value.copy(periodReminderDays = days))
    }
    
    fun updatePeriodReminderTime(time: String) {
        updateAndReschedule(settings.value.copy(periodReminderTime = time))
    }
    
    fun updateOvulationReminderEnabled(enabled: Boolean) {
        updateAndReschedule(settings.value.copy(ovulationReminderEnabled = enabled))
    }
    
    fun updateOvulationReminderDays(days: Int) {
        updateAndReschedule(settings.value.copy(ovulationReminderDays = days))
    }
    
    fun updatePmsReminderEnabled(enabled: Boolean) {
        updateAndReschedule(settings.value.copy(pmsReminderEnabled = enabled))
    }
    
    fun updatePmsReminderDays(days: Int) {
        updateAndReschedule(settings.value.copy(pmsReminderDays = days))
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val application = context.applicationContext as PeriodPalApplication
            return ReminderViewModel(application, SettingsRepository(context)) as T
        }
    }
}
