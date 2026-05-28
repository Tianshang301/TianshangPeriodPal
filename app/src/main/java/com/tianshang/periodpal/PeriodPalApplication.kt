package com.tianshang.periodpal

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.tianshang.periodpal.data.local.EncryptionKeyManager
import com.tianshang.periodpal.data.local.PeriodDatabase
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.service.ReminderScheduler
import com.tianshang.periodpal.utils.EncryptionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

class PeriodPalApplication : Application() {
    
    companion object {
        private const val TAG = "PeriodPalApp"
        lateinit var instance: PeriodPalApplication
            private set
    }
    
    @Volatile
    private var _database: PeriodDatabase? = null
    
    val database: PeriodDatabase
        get() = _database ?: throw IllegalStateException("Database not initialized. Call initDatabase() first.")
    
    val isDatabaseInitialized: Boolean
        get() = _database != null
    
    fun initDatabase() {
        if (_database != null) return
        
        val settingsRepo = SettingsRepository(instance)
        val settings = runBlocking(Dispatchers.IO) { settingsRepo.settings.first() }
        
        // First launch: no database file exists → default to encrypted
        val dbFile = getDatabasePath("period_pal_database")
        val isFirstLaunch = !dbFile.exists()
        
        if (isFirstLaunch && !settings.dbEncrypted) {
            // Generate passphrase before creating encrypted database
            EncryptionKeyManager.getOrCreatePassphrase(this)
            runBlocking(Dispatchers.IO) {
                settingsRepo.updateSettings(settings.copy(dbEncrypted = true))
            }
            _database = PeriodDatabase.getDatabase(this, encrypted = true)
        } else {
            _database = PeriodDatabase.getDatabase(this, encrypted = settings.dbEncrypted)
        }
    }
    
    fun reinitializeDatabase() {
        _database = null
        initDatabase()
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
        
        // Initialize database synchronously before any Activity accesses it
        initDatabase()
        
        // Schedule reminders asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val records = database.periodRecordDao().getAllRecordsSync()
                val symptoms = database.dailySymptomDao().getAllSymptoms().first()
                val settings = SettingsRepository(instance).settings.first()
                ReminderScheduler.scheduleReminders(instance, records, symptoms, settings)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to schedule reminders", e)
                ReminderScheduler.scheduleDailyCheck(instance)
            }
        }
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
