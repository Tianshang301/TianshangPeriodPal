package com.tianshang.periodpal

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.tianshang.periodpal.data.local.PeriodDatabase
import com.tianshang.periodpal.service.ReminderScheduler
import com.tianshang.periodpal.utils.EncryptionManager
import java.util.Locale

class PeriodPalApplication : Application() {
    
    companion object {
        lateinit var instance: PeriodPalApplication
            private set
    }
    
    val database: PeriodDatabase by lazy {
        try {
            PeriodDatabase.getDatabase(this)
        } catch (e: Exception) {
            PeriodDatabase.recreateDatabase(this)
        }
    }
    
    val encryptionManager: EncryptionManager by lazy {
        EncryptionManager(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        val savedLang = getSharedPreferences("periodpal_prefs", MODE_PRIVATE)
            .getString("language", null)
        savedLang?.let { applyLanguage(it) }
        
        ReminderScheduler.createNotificationChannels(this)
    }
    
    fun applyLanguage(language: String) {
        val locale = when (language) {
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "en" -> Locale.ENGLISH
            "ja" -> Locale.JAPANESE
            "ko" -> Locale.KOREAN
            "fr" -> Locale.FRENCH
            "es" -> Locale("es")
            "ar" -> Locale("ar")
            else -> return
        }
        Locale.setDefault(locale)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
    }
}
