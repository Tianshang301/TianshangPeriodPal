package com.tianshang.periodpal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.data.repository.UserSettings
import com.tianshang.periodpal.utils.PredictionEngine
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ReminderScheduler {
    
    companion object {
        const val CHANNEL_PERIOD = "period_reminder"
        const val CHANNEL_OVULATION = "ovulation_reminder"
        const val CHANNEL_PMS = "pms_reminder"
        const val CHANNEL_CUSTOM = "custom_reminder"
        const val TAG_DAILY_CHECK = "daily_reminder_check"
        
        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channels = listOf(
                    NotificationChannel(
                        CHANNEL_PERIOD,
                        context.getString(R.string.channel_period_reminder),
                        NotificationManager.IMPORTANCE_HIGH
                    ),
                    NotificationChannel(
                        CHANNEL_OVULATION,
                        context.getString(R.string.channel_ovulation_reminder),
                        NotificationManager.IMPORTANCE_HIGH
                    ),
                    NotificationChannel(
                        CHANNEL_PMS,
                        context.getString(R.string.channel_pms_reminder),
                        NotificationManager.IMPORTANCE_HIGH
                    ),
                    NotificationChannel(
                        CHANNEL_CUSTOM,
                        context.getString(R.string.channel_custom_reminder),
                        NotificationManager.IMPORTANCE_HIGH
                    )
                )
                
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannels(channels)
            }
        }
        
        fun scheduleDailyCheck(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG_DAILY_CHECK)
            
            val now = java.time.LocalDateTime.now()
            val targetTime = LocalTime.of(8, 0)
            var nextRun = now.toLocalDate().atTime(targetTime)
            if (nextRun.isBefore(now) || nextRun.isEqual(now)) {
                nextRun = nextRun.plusDays(1)
            }
            
            val delayMinutes = java.time.Duration.between(now, nextRun).toMinutes()
            
            val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .addTag(TAG_DAILY_CHECK)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                TAG_DAILY_CHECK,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
        
        fun scheduleReminders(
            context: Context,
            records: List<PeriodRecord>,
            symptoms: List<DailySymptom>,
            settings: UserSettings
        ) {
            scheduleDailyCheck(context)
        }
    }
}

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val app = applicationContext as PeriodPalApplication
        val records = app.database.periodRecordDao().getAllRecordsSync()
        val symptoms = app.database.dailySymptomDao().getAllSymptoms().first()
        val settings = SettingsRepository(applicationContext).settings.first()
        
        if (!settings.periodReminderEnabled && !settings.ovulationReminderEnabled && !settings.pmsReminderEnabled) {
            return Result.success()
        }
        
        val predictionEngine = PredictionEngine()
        val predictions = predictionEngine.predictNextCycles(records, symptoms, settings.lutealPhaseLength)
        if (predictions.isEmpty()) return Result.success()
        
        val today = LocalDate.now()
        val prediction = predictions.first()
        
        if (settings.periodReminderEnabled) {
            val reminderDate = prediction.periodStartDate.minusDays(settings.periodReminderDays.toLong())
            if (today == reminderDate || today.isAfter(reminderDate)) {
                showNotification(
                    "period",
                    applicationContext.getString(R.string.notif_period_title),
                    applicationContext.getString(R.string.notif_period_message, settings.periodReminderDays)
                )
            }
        }
        
        if (settings.ovulationReminderEnabled) {
            val reminderDate = prediction.ovulationDate.minusDays(settings.ovulationReminderDays.toLong())
            if (today == reminderDate || today.isAfter(reminderDate)) {
                showNotification(
                    "ovulation",
                    applicationContext.getString(R.string.notif_ovulation_title),
                    applicationContext.getString(R.string.notif_ovulation_message, settings.ovulationReminderDays)
                )
            }
        }
        
        if (settings.pmsReminderEnabled) {
            val reminderDate = prediction.periodStartDate.minusDays(settings.pmsReminderDays.toLong())
            if (today == reminderDate || today.isAfter(reminderDate)) {
                showNotification(
                    "pms",
                    applicationContext.getString(R.string.notif_pms_title),
                    applicationContext.getString(R.string.notif_pms_message, settings.pmsReminderDays)
                )
            }
        }
        
        return Result.success()
    }
    
    private fun showNotification(type: String, title: String, message: String) {
        val channelId = when (type) {
            "period" -> ReminderScheduler.CHANNEL_PERIOD
            "ovulation" -> ReminderScheduler.CHANNEL_OVULATION
            "pms" -> ReminderScheduler.CHANNEL_PMS
            else -> ReminderScheduler.CHANNEL_CUSTOM
        }
        
        val intent = Intent(applicationContext, com.tianshang.periodpal.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            type.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(type.hashCode(), notification)
    }
}
