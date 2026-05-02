package com.tianshang.periodpal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.tianshang.periodpal.MainActivity
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord
import com.tianshang.periodpal.data.repository.UserSettings
import com.tianshang.periodpal.utils.PredictionEngine
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ReminderScheduler {
    
    companion object {
        const val CHANNEL_PERIOD = "period_reminder"
        const val CHANNEL_OVULATION = "ovulation_reminder"
        const val CHANNEL_PMS = "pms_reminder"
        const val CHANNEL_CUSTOM = "custom_reminder"
        
        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channels = listOf(
                    NotificationChannel(
                        CHANNEL_PERIOD,
                        context.getString(R.string.channel_period_reminder),
                        NotificationManager.IMPORTANCE_DEFAULT
                    ),
                    NotificationChannel(
                        CHANNEL_OVULATION,
                        context.getString(R.string.channel_ovulation_reminder),
                        NotificationManager.IMPORTANCE_DEFAULT
                    ),
                    NotificationChannel(
                        CHANNEL_PMS,
                        context.getString(R.string.channel_pms_reminder),
                        NotificationManager.IMPORTANCE_DEFAULT
                    ),
                    NotificationChannel(
                        CHANNEL_CUSTOM,
                        context.getString(R.string.channel_custom_reminder),
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
                
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannels(channels)
            }
        }
        
        fun scheduleReminders(
            context: Context,
            records: List<PeriodRecord>,
            symptoms: List<DailySymptom>,
            settings: UserSettings
        ) {
            WorkManager.getInstance(context).cancelAllWorkByTag("period_reminder")
            WorkManager.getInstance(context).cancelAllWorkByTag("ovulation_reminder")
            WorkManager.getInstance(context).cancelAllWorkByTag("pms_reminder")
            
            val predictionEngine = PredictionEngine()
            val predictions = predictionEngine.predictNextCycles(records, symptoms, settings.lutealPhaseLength)
            
            if (predictions.isEmpty()) return
            
            val nextPrediction = predictions.first()
            
            if (settings.periodReminderEnabled) {
                val periodTimeParts = settings.periodReminderTime.split(":")
                val periodHour = periodTimeParts.getOrNull(0)?.toIntOrNull() ?: 8
                val periodMinute = periodTimeParts.getOrNull(1)?.toIntOrNull() ?: 0
                val periodReminderTime = LocalTime.of(periodHour, periodMinute)
                
                val periodDate = nextPrediction.periodStartDate.minusDays(settings.periodReminderDays.toLong())
                val triggerDateTime = LocalDateTime.of(periodDate, periodReminderTime)
                if (triggerDateTime.isAfter(LocalDateTime.now())) {
                    scheduleReminder(
                        context = context,
                        reminderType = "period",
                        tag = "period_reminder",
                        triggerTime = triggerDateTime,
                        title = context.getString(R.string.notif_period_title),
                        message = context.getString(R.string.notif_period_message, settings.periodReminderDays)
                    )
                }
            }
            
            if (settings.ovulationReminderEnabled) {
                val ovulationTimeParts = settings.ovulationReminderTime.split(":")
                val ovulationHour = ovulationTimeParts.getOrNull(0)?.toIntOrNull() ?: 9
                val ovulationMinute = ovulationTimeParts.getOrNull(1)?.toIntOrNull() ?: 0
                val ovulationReminderTime = LocalTime.of(ovulationHour, ovulationMinute)
                
                val ovulationDate = nextPrediction.ovulationDate.minusDays(settings.ovulationReminderDays.toLong())
                val triggerDateTime = LocalDateTime.of(ovulationDate, ovulationReminderTime)
                if (triggerDateTime.isAfter(LocalDateTime.now())) {
                    scheduleReminder(
                        context = context,
                        reminderType = "ovulation",
                        tag = "ovulation_reminder",
                        triggerTime = triggerDateTime,
                        title = context.getString(R.string.notif_ovulation_title),
                        message = context.getString(R.string.notif_ovulation_message, settings.ovulationReminderDays)
                    )
                }
            }
            
            if (settings.pmsReminderEnabled) {
                val pmsTimeParts = settings.pmsReminderTime.split(":")
                val pmsHour = pmsTimeParts.getOrNull(0)?.toIntOrNull() ?: 8
                val pmsMinute = pmsTimeParts.getOrNull(1)?.toIntOrNull() ?: 0
                val pmsReminderTime = LocalTime.of(pmsHour, pmsMinute)
                
                val pmsDate = nextPrediction.periodStartDate.minusDays(settings.pmsReminderDays.toLong())
                val triggerDateTime = LocalDateTime.of(pmsDate, pmsReminderTime)
                if (triggerDateTime.isAfter(LocalDateTime.now())) {
                    scheduleReminder(
                        context = context,
                        reminderType = "pms",
                        tag = "pms_reminder",
                        triggerTime = triggerDateTime,
                        title = context.getString(R.string.notif_pms_title),
                        message = context.getString(R.string.notif_pms_message, settings.pmsReminderDays)
                    )
                }
            }
        }
        
        private fun scheduleReminder(
            context: Context,
            reminderType: String,
            tag: String,
            triggerTime: LocalDateTime,
            title: String,
            message: String
        ) {
            val delay = Duration.between(LocalDateTime.now(), triggerTime)
            
            if (delay.isNegative) return
            
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(
                    "type" to reminderType,
                    "title" to title,
                    "message" to message
                ))
                .addTag(tag)
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                "reminder_${reminderType}_${System.currentTimeMillis()}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    
    override fun doWork(): Result {
        val type = inputData.getString("type") ?: return Result.failure()
        val title = inputData.getString("title") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()
        
        showNotification(type, title, message)
        
        return Result.success()
    }
    
    private fun showNotification(type: String, title: String, message: String) {
        val channelId = when (type) {
            "period" -> ReminderScheduler.CHANNEL_PERIOD
            "ovulation" -> ReminderScheduler.CHANNEL_OVULATION
            "pms" -> ReminderScheduler.CHANNEL_PMS
            else -> ReminderScheduler.CHANNEL_CUSTOM
        }
        
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
