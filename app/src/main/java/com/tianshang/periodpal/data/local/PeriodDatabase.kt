package com.tianshang.periodpal.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tianshang.periodpal.data.model.BmiRecord
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [PeriodRecord::class, DailySymptom::class, BmiRecord::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PeriodDatabase : RoomDatabase() {
    abstract fun periodRecordDao(): PeriodRecordDao
    abstract fun dailySymptomDao(): DailySymptomDao
    abstract fun bmiRecordDao(): BmiRecordDao
    
    companion object {
        private const val TAG = "PeriodDatabase"
        
        @Volatile
        private var INSTANCE: PeriodDatabase? = null
        
        fun closeInstance() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
        
        fun getDatabase(context: Context, encrypted: Boolean = false): PeriodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context, encrypted)
                INSTANCE = instance
                instance
            }
        }
        
        private fun buildDatabase(context: Context, encrypted: Boolean): PeriodDatabase {
            val builder = Room.databaseBuilder(
                context.applicationContext,
                PeriodDatabase::class.java,
                "period_pal_database"
            )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigrationOnDowngrade()
            
            if (encrypted) {
                val passphrase = EncryptionKeyManager.getOrCreatePassphrase(context)
                val factory = SupportFactory(passphrase)
                builder.openHelperFactory(factory)
            }
            
            return builder.build()
        }
        
        fun recreateDatabase(context: Context, encrypted: Boolean = false): PeriodDatabase {
            synchronized(this) {
                closeInstance()
                context.deleteDatabase("period_pal_database")
                return buildDatabase(context, encrypted).also { INSTANCE = it }
            }
        }
        
        // Migration from version 1 to 2
        // Added DailySymptom entity and isDeleted field to PeriodRecord
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create daily_symptoms table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_symptoms` (
                        `date` TEXT NOT NULL,
                        `symptoms` TEXT NOT NULL DEFAULT '[]',
                        `sexualActivity` INTEGER,
                        `ovulationTestResult` TEXT,
                        `cervicalMucus` TEXT,
                        `bodyTemperature` REAL,
                        `notes` TEXT,
                        PRIMARY KEY(`date`)
                    )
                    """.trimIndent()
                )
                
                // Add isDeleted and deletedAt columns to period_records
                database.execSQL("ALTER TABLE `period_records` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `period_records` ADD COLUMN `deletedAt` INTEGER")
            }
        }
    }
}
