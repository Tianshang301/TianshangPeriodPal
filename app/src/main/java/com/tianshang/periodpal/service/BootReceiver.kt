package com.tianshang.periodpal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tianshang.periodpal.PeriodPalApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val application = context.applicationContext as PeriodPalApplication
            
            CoroutineScope(Dispatchers.IO).launch {
                val repository = application.database.periodRecordDao()
                val records = repository.getAllRecordsSync()
                
                // Reschedule reminders after reboot
                ReminderScheduler.scheduleReminders(context, records)
            }
        }
    }
}
