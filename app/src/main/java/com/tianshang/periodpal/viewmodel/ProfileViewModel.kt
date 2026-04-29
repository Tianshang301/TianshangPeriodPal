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

class ProfileViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<UserSettings> = settingsRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        UserSettings()
    )
    
    fun toggleAppLock(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(appLockEnabled = enabled))
        }
    }
    
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(
                current.copy(
                    darkMode = enabled,
                    darkModeFollowSystem = false
                )
            )
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(SettingsRepository(context)) as T
        }
    }
}
