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

                val header = ByteArray(12)
                val read = inputStream.read(header)
                inputStream.close()

                if (read < 4) return@withContext null
                val isJpeg = header[0] == 0xFF.toByte() && header[1] == 0xD8.toByte() && header[2] == 0xFF.toByte()
                val isPng = header[0] == 0x89.toByte() && header[1] == 0x50.toByte() && header[2] == 0x4E.toByte() && header[3] == 0x47.toByte()
                val isWebP = read >= 12 && header[8] == 0x57.toByte() && header[9] == 0x45.toByte() && header[10] == 0x42.toByte() && header[11] == 0x50.toByte()
                if (!isJpeg && !isPng && !isWebP) return@withContext null

                val reOpenStream = context.contentResolver.openInputStream(contentUri) ?: return@withContext null
                val maxSize = 10 * 1024 * 1024
                val dir = File(context.filesDir, "backgrounds")
                dir.mkdirs()
                val file = File(dir, "background_image.jpg")
                file.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var totalBytes = 0L
                    var bytesRead: Int
                    while (reOpenStream.read(buffer).also { bytesRead = it } != -1) {
                        totalBytes += bytesRead
                        if (totalBytes > maxSize) {
                            reOpenStream.close()
                            file.delete()
                            return@withContext null
                        }
                        output.write(buffer, 0, bytesRead)
                    }
                }
                reOpenStream.close()
                Uri.fromFile(file).toString()
            } catch (_: Exception) {
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
    
    fun updateBackgroundLockDelay(delaySeconds: Int) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(appLockBackgroundDelay = delaySeconds))
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(context, SettingsRepository(context)) as T
        }
    }
}
