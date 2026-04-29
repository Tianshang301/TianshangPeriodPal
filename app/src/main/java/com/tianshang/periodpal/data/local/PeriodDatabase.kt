package com.tianshang.periodpal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tianshang.periodpal.data.model.BmiRecord
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord

@Database(
    entities = [PeriodRecord::class, DailySymptom::class, BmiRecord::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PeriodDatabase : RoomDatabase() {
    abstract fun periodRecordDao(): PeriodRecordDao
    abstract fun dailySymptomDao(): DailySymptomDao
    abstract fun bmiRecordDao(): BmiRecordDao
    
    companion object {
        @Volatile
        private var INSTANCE: PeriodDatabase? = null
        
        fun getDatabase(context: Context): PeriodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context)
                INSTANCE = instance
                instance
            }
        }
        
        private fun buildDatabase(context: Context): PeriodDatabase {
            return try {
                Room.databaseBuilder(
                    context.applicationContext,
                    PeriodDatabase::class.java,
                    "period_pal_database"
                ).build()
            } catch (e: Exception) {
                // 数据库损坏，删除并重建
                context.deleteDatabase("period_pal_database")
                Room.databaseBuilder(
                    context.applicationContext,
                    PeriodDatabase::class.java,
                    "period_pal_database"
                ).build()
            }
        }
        
        fun recreateDatabase(context: Context): PeriodDatabase {
            synchronized(this) {
                context.deleteDatabase("period_pal_database")
                INSTANCE = null
                return buildDatabase(context)
            }
        }
    }
}
