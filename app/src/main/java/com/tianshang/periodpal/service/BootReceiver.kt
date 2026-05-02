package com.tianshang.periodpal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val application = context.applicationContext as PeriodPalApplication
            
            CoroutineScope(Dispatchers.IO).launch {
                val records = application.database.periodRecordDao().getAllRecordsSync()
                val symptoms = application.database.dailySymptomDao().getAllSymptoms().first()
                val settings = SettingsRepository(context).settings.first()
                
                ReminderScheduler.scheduleReminders(context, records, symptoms, settings)
            }
        }
    }
}
