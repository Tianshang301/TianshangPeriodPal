package com.tianshang.periodpal.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {
    
    private object PreferencesKeys {
        val CYCLE_LENGTH = intPreferencesKey("cycle_length")
        val LUTEAL_PHASE_LENGTH = intPreferencesKey("luteal_phase_length")
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val BACKGROUND_IMAGE_URI = stringPreferencesKey("background_image_uri")
        val BACKGROUND_TRANSPARENCY = intPreferencesKey("background_transparency")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val APP_LOCK_PASSWORD_HASH = stringPreferencesKey("app_lock_password_hash")
        val LANGUAGE = stringPreferencesKey("language")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val TERMS_ACCEPTED = booleanPreferencesKey("terms_accepted")
        
        // Reminder settings
        val PERIOD_REMINDER_ENABLED = booleanPreferencesKey("period_reminder_enabled")
        val PERIOD_REMINDER_DAYS = intPreferencesKey("period_reminder_days")
        val PERIOD_REMINDER_TIME = stringPreferencesKey("period_reminder_time")
        
        val OVULATION_REMINDER_ENABLED = booleanPreferencesKey("ovulation_reminder_enabled")
        val OVULATION_REMINDER_DAYS = intPreferencesKey("ovulation_reminder_days")
        
        val PMS_REMINDER_ENABLED = booleanPreferencesKey("pms_reminder_enabled")
        val PMS_REMINDER_DAYS = intPreferencesKey("pms_reminder_days")
        
        // Theme settings
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val DARK_MODE_FOLLOW_SYSTEM = booleanPreferencesKey("dark_mode_follow_system")
        val PREVENT_SCREENSHOT = booleanPreferencesKey("prevent_screenshot")
    }
    
    val settings: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            cycleLength = preferences[PreferencesKeys.CYCLE_LENGTH] ?: 28,
            lutealPhaseLength = preferences[PreferencesKeys.LUTEAL_PHASE_LENGTH] ?: 14,
            themeColor = preferences[PreferencesKeys.THEME_COLOR] ?: "#FFB6C1",
            backgroundImageUri = preferences[PreferencesKeys.BACKGROUND_IMAGE_URI],
            backgroundTransparency = preferences[PreferencesKeys.BACKGROUND_TRANSPARENCY] ?: 50,
            appLockEnabled = preferences[PreferencesKeys.APP_LOCK_ENABLED] ?: false,
            appLockPasswordHash = preferences[PreferencesKeys.APP_LOCK_PASSWORD_HASH],
            language = preferences[PreferencesKeys.LANGUAGE],
            firstLaunch = preferences[PreferencesKeys.FIRST_LAUNCH] ?: true,
            termsAccepted = preferences[PreferencesKeys.TERMS_ACCEPTED] ?: false,
            periodReminderEnabled = preferences[PreferencesKeys.PERIOD_REMINDER_ENABLED] ?: true,
            periodReminderDays = preferences[PreferencesKeys.PERIOD_REMINDER_DAYS] ?: 1,
            periodReminderTime = preferences[PreferencesKeys.PERIOD_REMINDER_TIME] ?: "08:00",
            ovulationReminderEnabled = preferences[PreferencesKeys.OVULATION_REMINDER_ENABLED] ?: true,
            ovulationReminderDays = preferences[PreferencesKeys.OVULATION_REMINDER_DAYS] ?: 1,
            pmsReminderEnabled = preferences[PreferencesKeys.PMS_REMINDER_ENABLED] ?: true,
            pmsReminderDays = preferences[PreferencesKeys.PMS_REMINDER_DAYS] ?: 5,
            darkMode = preferences[PreferencesKeys.DARK_MODE] ?: false,
            darkModeFollowSystem = preferences[PreferencesKeys.DARK_MODE_FOLLOW_SYSTEM] ?: true,
            preventScreenshot = preferences[PreferencesKeys.PREVENT_SCREENSHOT] ?: false
        )
    }
    
    suspend fun updateSettings(settings: UserSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CYCLE_LENGTH] = settings.cycleLength
            preferences[PreferencesKeys.LUTEAL_PHASE_LENGTH] = settings.lutealPhaseLength
            preferences[PreferencesKeys.THEME_COLOR] = settings.themeColor
            if (settings.backgroundImageUri != null) {
                preferences[PreferencesKeys.BACKGROUND_IMAGE_URI] = settings.backgroundImageUri
            } else {
                preferences.remove(PreferencesKeys.BACKGROUND_IMAGE_URI)
            }
            preferences[PreferencesKeys.BACKGROUND_TRANSPARENCY] = settings.backgroundTransparency
            preferences[PreferencesKeys.APP_LOCK_ENABLED] = settings.appLockEnabled
            settings.appLockPasswordHash?.let { preferences[PreferencesKeys.APP_LOCK_PASSWORD_HASH] = it }
            settings.language?.let { preferences[PreferencesKeys.LANGUAGE] = it }
            preferences[PreferencesKeys.FIRST_LAUNCH] = settings.firstLaunch
            preferences[PreferencesKeys.TERMS_ACCEPTED] = settings.termsAccepted
            preferences[PreferencesKeys.PERIOD_REMINDER_ENABLED] = settings.periodReminderEnabled
            preferences[PreferencesKeys.PERIOD_REMINDER_DAYS] = settings.periodReminderDays
            preferences[PreferencesKeys.PERIOD_REMINDER_TIME] = settings.periodReminderTime
            preferences[PreferencesKeys.OVULATION_REMINDER_ENABLED] = settings.ovulationReminderEnabled
            preferences[PreferencesKeys.OVULATION_REMINDER_DAYS] = settings.ovulationReminderDays
            preferences[PreferencesKeys.PMS_REMINDER_ENABLED] = settings.pmsReminderEnabled
            preferences[PreferencesKeys.PMS_REMINDER_DAYS] = settings.pmsReminderDays
            preferences[PreferencesKeys.DARK_MODE] = settings.darkMode
            preferences[PreferencesKeys.DARK_MODE_FOLLOW_SYSTEM] = settings.darkModeFollowSystem
            preferences[PreferencesKeys.PREVENT_SCREENSHOT] = settings.preventScreenshot
        }
    }
    
    suspend fun acceptTerms() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TERMS_ACCEPTED] = true
            preferences[PreferencesKeys.FIRST_LAUNCH] = false
        }
    }
}

data class UserSettings(
    val cycleLength: Int = 28,
    val lutealPhaseLength: Int = 14,
    val themeColor: String = "#FFB6C1",
    val backgroundImageUri: String? = null,
    val backgroundTransparency: Int = 50,
    val appLockEnabled: Boolean = false,
    val appLockPasswordHash: String? = null,
    val language: String? = null,
    val firstLaunch: Boolean = true,
    val termsAccepted: Boolean = false,
    val periodReminderEnabled: Boolean = true,
    val periodReminderDays: Int = 1,
    val periodReminderTime: String = "08:00",
    val ovulationReminderEnabled: Boolean = true,
    val ovulationReminderDays: Int = 1,
    val pmsReminderEnabled: Boolean = true,
    val pmsReminderDays: Int = 5,
    val darkMode: Boolean = false,
    val darkModeFollowSystem: Boolean = true,
    val preventScreenshot: Boolean = false
)
