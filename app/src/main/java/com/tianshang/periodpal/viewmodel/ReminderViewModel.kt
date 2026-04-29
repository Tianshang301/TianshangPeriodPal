package com.tianshang.periodpal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.data.repository.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<UserSettings> = settingsRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        UserSettings()
    )
    
    fun updatePeriodReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(periodReminderEnabled = enabled))
        }
    }
    
    fun updatePeriodReminderDays(days: Int) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(periodReminderDays = days))
        }
    }
    
    fun updatePeriodReminderTime(time: String) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(periodReminderTime = time))
        }
    }
    
    fun updateOvulationReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(ovulationReminderEnabled = enabled))
        }
    }
    
    fun updateOvulationReminderDays(days: Int) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(ovulationReminderDays = days))
        }
    }
    
    fun updatePmsReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(pmsReminderEnabled = enabled))
        }
    }
    
    fun updatePmsReminderDays(days: Int) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(pmsReminderDays = days))
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReminderViewModel(SettingsRepository(context)) as T
        }
    }
}
