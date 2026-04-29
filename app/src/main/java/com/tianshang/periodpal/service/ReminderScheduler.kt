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
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
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
                        "经期提醒",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ),
                    NotificationChannel(
                        CHANNEL_OVULATION,
                        "排卵期提醒",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ),
                    NotificationChannel(
                        CHANNEL_PMS,
                        "经前综合征提醒",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ),
                    NotificationChannel(
                        CHANNEL_CUSTOM,
                        "自定义提醒",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
                
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannels(channels)
            }
        }
        
        fun scheduleReminders(context: Context, records: List<com.tianshang.periodpal.data.model.PeriodRecord>) {
            // Implementation for scheduling reminders based on predictions
            // This would calculate next period dates and schedule WorkManager tasks
        }
        
        fun scheduleReminder(
            context: Context,
            reminderType: String,
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
