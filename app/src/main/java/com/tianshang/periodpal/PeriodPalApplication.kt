package com.tianshang.periodpal

import android.app.Application
import com.tianshang.periodpal.data.local.PeriodDatabase
import com.tianshang.periodpal.utils.EncryptionManager

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
    }
}
