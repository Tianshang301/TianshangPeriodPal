package com.tianshang.periodpal.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.data.repository.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SettingsViewModel(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<UserSettings> = settingsRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        UserSettings()
    )
    
    fun setLanguage(language: String) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(language = language))
        }
    }
    
    fun setThemeColor(color: String) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(themeColor = color))
        }
    }
    
    fun setBackgroundImage(contentUri: Uri) {
        viewModelScope.launch {
            val internalUri = copyToInternalStorage(contentUri)
            if (internalUri != null) {
                val current = settings.value
                settingsRepository.updateSettings(current.copy(backgroundImageUri = internalUri))
            }
        }
    }
    
    private suspend fun copyToInternalStorage(contentUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(contentUri) ?: return@withContext null
                val dir = File(context.filesDir, "backgrounds")
                dir.mkdirs()
                val file = File(dir, "background_image.jpg")
                file.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                inputStream.close()
                Uri.fromFile(file).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    fun removeBackgroundImage() {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(backgroundImageUri = null))
        }
    }
    
    fun setBackgroundTransparency(transparency: Int) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(backgroundTransparency = transparency))
        }
    }
    
    fun togglePreventScreenshot(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(preventScreenshot = enabled))
        }
    }
    
    fun toggleAppLock(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(appLockEnabled = enabled))
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(context, SettingsRepository(context)) as T
        }
    }
}
