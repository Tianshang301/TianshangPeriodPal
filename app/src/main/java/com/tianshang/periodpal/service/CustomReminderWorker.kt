package com.tianshang.periodpal.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.tianshang.periodpal.MainActivity
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.model.CustomReminder
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class CustomReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val message = inputData.getString("message") ?: ""
        
        showNotification(title, message)
        return Result.success()
    }
    
    private fun showNotification(title: String, message: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            title.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, ReminderScheduler.CHANNEL_CUSTOM)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify("custom_${title.hashCode()}".hashCode(), notification)
    }
    
    companion object {
        fun schedule(context: Context, reminder: CustomReminder) {
            val now = LocalDateTime.now()
            val reminderTime = reminder.getLocalDateTime()
            
            if (reminderTime.isBefore(now) || !reminder.enabled) return
            
            val delay = Duration.between(now, reminderTime).toMillis()
            
            val inputData = workDataOf(
                "title" to reminder.title,
                "message" to reminder.message
            )
            
            val workRequest = OneTimeWorkRequestBuilder<CustomReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("custom_reminder_${reminder.id}")
                .build()
            
            WorkManager.getInstance(context).enqueue(workRequest)
        }
        
        fun cancel(context: Context, reminderId: Long) {
            WorkManager.getInstance(context).cancelAllWorkByTag("custom_reminder_$reminderId")
        }
        
        fun rescheduleAll(context: Context, reminders: List<CustomReminder>) {
            WorkManager.getInstance(context).cancelAllWorkByTag("custom_reminder")
            reminders.filter { it.enabled }.forEach { schedule(context, it) }
        }
    }
}
