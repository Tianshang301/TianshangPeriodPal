package com.tianshang.periodpal

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.ui.navigation.PeriodPalNavHost
import com.tianshang.periodpal.ui.theme.PeriodPalTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
    private lateinit var settingsRepository: SettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        settingsRepository = SettingsRepository(this)
        
        // 同步应用语言和反截屏设置
        lifecycleScope.launch {
            val settings = withContext(Dispatchers.IO) {
                settingsRepository.settings.first()
            }
            // 反截屏设置在 onCreate 中同步应用
            updateSecureFlag(settings.preventScreenshot)
            // 同步应用语言设置
            settings.language?.let { language ->
                applyLanguage(language)
            }
        }
        
        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = null)
            
            val darkTheme = when {
                settings == null -> isSystemInDarkTheme()
                settings!!.darkModeFollowSystem -> isSystemInDarkTheme()
                else -> settings!!.darkMode
            }
            
            // 监听反截屏设置变化
            val preventScreenshot = settings?.preventScreenshot ?: false
            updateSecureFlag(preventScreenshot)
            
            PeriodPalTheme(
                darkTheme = darkTheme,
                themeColor = settings?.themeColor
            ) {
                PeriodPalNavHost()
            }
        }
    }
    
    private fun applyLanguage(language: String) {
        try {
            val locale = when (language) {
                "zh" -> Locale.SIMPLIFIED_CHINESE
                "en" -> Locale.ENGLISH
                "ja" -> Locale.JAPANESE
                "ko" -> Locale.KOREAN
                "fr" -> Locale.FRENCH
                "es" -> Locale("es")
                "ar" -> Locale("ar")
                else -> Locale.SIMPLIFIED_CHINESE
            }
            val localeList = LocaleListCompat.create(locale)
            AppCompatDelegate.setApplicationLocales(localeList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateSecureFlag(enabled: Boolean) {
        if (enabled) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
