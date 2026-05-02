package com.tianshang.periodpal

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.ui.navigation.PeriodPalNavHost
import com.tianshang.periodpal.ui.theme.PeriodPalTheme
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
    private lateinit var settingsRepository: SettingsRepository
    
    override fun attachBaseContext(newBase: Context) {
        val locale = getSavedLocale(newBase)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        settingsRepository = SettingsRepository(this)
        
        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = null)
            
            val darkTheme = when {
                settings == null -> isSystemInDarkTheme()
                settings!!.darkModeFollowSystem -> isSystemInDarkTheme()
                else -> settings!!.darkMode
            }
            
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
    
    private fun getSavedLocale(context: Context): Locale {
        // 优先从 SharedPreferences 读取（LanguageScreen 同步写入）
        val prefs = context.getSharedPreferences("periodpal_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", null) ?: run {
            // 回退到 AppCompat 的 locales（可能通过 setApplicationLocales 设置）
            val appLocales = AppCompatDelegate.getApplicationLocales()
            if (!appLocales.isEmpty) return appLocales[0] ?: Locale.getDefault()
            return@run null
        }
        return when (lang) {
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "en" -> Locale.ENGLISH
            "ja" -> Locale.JAPANESE
            "ko" -> Locale.KOREAN
            "fr" -> Locale.FRENCH
            "es" -> Locale("es")
            "ar" -> Locale("ar")
            else -> Locale.getDefault()
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
