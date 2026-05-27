package com.tianshang.periodpal.utils

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tianshang.periodpal.data.local.PeriodDatabase
import com.tianshang.periodpal.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class DatabaseMigrationManager(private val context: Context) {
    
    sealed class MigrationResult {
        object Success : MigrationResult()
        data class Failure(val reason: String) : MigrationResult()
    }
    
    suspend fun migrateToEncrypted(): MigrationResult = withContext(Dispatchers.IO) {
        val currentDbPath = context.getDatabasePath("period_pal_database")
        val currentDbWal = File(currentDbPath.path + "-wal")
        val currentDbShm = File(currentDbPath.path + "-shm")
        val backupFile = File(context.cacheDir, "plain_backup.db")
        val backupWal = File(context.cacheDir, "plain_backup.db-wal")
        val backupShm = File(context.cacheDir, "plain_backup.db-shm")
        
        try {
            // 1. Checkpoint WAL to ensure all data is in the main file
            try {
                val db = PeriodDatabase.getDatabase(context, encrypted = false)
                val sqliteDb: SupportSQLiteDatabase = db.openHelper.writableDatabase
                sqliteDb.execSQL("PRAGMA wal_checkpoint(TRUNCATE)")
            } catch (_: Exception) {
                // If checkpoint fails, continue with file copy
            }
            
            // 2. Close current connection completely
            PeriodDatabase.closeInstance()
            
            // 3. Backup current database files
            if (currentDbPath.exists()) {
                currentDbPath.copyTo(backupFile, overwrite = true)
            }
            if (currentDbWal.exists()) {
                currentDbWal.copyTo(backupWal, overwrite = true)
            }
            if (currentDbShm.exists()) {
                currentDbShm.copyTo(backupShm, overwrite = true)
            }
            
            // 4. Count records in backup for verification
            val plainDb = android.database.sqlite.SQLiteDatabase.openDatabase(
                backupFile.absolutePath, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY
            )
            val periodCount = queryCount(plainDb, "SELECT COUNT(*) FROM period_records WHERE isDeleted = 0")
            val symptomCount = queryCount(plainDb, "SELECT COUNT(*) FROM daily_symptoms")
            val bmiCount = queryCount(plainDb, "SELECT COUNT(*) FROM bmi_records")
            plainDb.close()
            
            // 5. Delete current database
            context.deleteDatabase("period_pal_database")
            
            // 6. Create new encrypted database
            val encryptedDb = PeriodDatabase.getDatabase(context, encrypted = true)
            
            // 7. Attach plaintext backup and copy data
            val encSqlDb: SupportSQLiteDatabase = encryptedDb.openHelper.writableDatabase
            val escapedPath = backupFile.absolutePath.replace("'", "''")
            encSqlDb.execSQL("ATTACH DATABASE '$escapedPath' AS plaintext KEY ''")
            
            // Copy data for each table
            encSqlDb.execSQL("INSERT INTO period_records SELECT * FROM plaintext.period_records")
            encSqlDb.execSQL("INSERT INTO daily_symptoms SELECT * FROM plaintext.daily_symptoms")
            encSqlDb.execSQL("INSERT INTO bmi_records SELECT * FROM plaintext.bmi_records")
            
            // Detach plaintext database
            encSqlDb.execSQL("DETACH DATABASE plaintext")
            
            // 8. Verify record counts in encrypted database
            val encPeriodCount = encSqlDb.query("SELECT COUNT(*) FROM period_records WHERE isDeleted = 0").use {
                it.moveToFirst(); it.getInt(0)
            }
            val encSymptomCount = encSqlDb.query("SELECT COUNT(*) FROM daily_symptoms").use {
                it.moveToFirst(); it.getInt(0)
            }
            val encBmiCount = encSqlDb.query("SELECT COUNT(*) FROM bmi_records").use {
                it.moveToFirst(); it.getInt(0)
            }
            
            if (encPeriodCount != periodCount || encSymptomCount != symptomCount || encBmiCount != bmiCount) {
                throw Exception("Record count mismatch: period($periodCount->$encPeriodCount), symptom($symptomCount->$encSymptomCount), bmi($bmiCount->$encBmiCount)")
            }
            
            // 9. Update settings
            val settingsRepo = SettingsRepository(context)
            val settings = settingsRepo.settings.first()
            settingsRepo.updateSettings(settings.copy(dbEncrypted = true))
            
            // 10. Cleanup backup
            cleanupBackup(backupFile, backupWal, backupShm)
            
            MigrationResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            rollbackFromBackup(currentDbPath, currentDbWal, currentDbShm, backupFile, backupWal, backupShm)
            MigrationResult.Failure(e.message ?: "未知错误")
        }
    }
    
    private fun queryCount(db: android.database.sqlite.SQLiteDatabase, sql: String): Int {
        val cursor = db.rawQuery(sql, null)
        return try {
            cursor.moveToFirst(); cursor.getInt(0)
        } finally {
            cursor.close()
        }
    }
    
    private fun cleanupBackup(backupFile: File, backupWal: File, backupShm: File) {
        try { backupFile.delete() } catch (_: Exception) {}
        try { backupWal.delete() } catch (_: Exception) {}
        try { backupShm.delete() } catch (_: Exception) {}
    }
    
    private suspend fun rollbackFromBackup(
        dbPath: File, dbWal: File, dbShm: File,
        backupFile: File, backupWal: File, backupShm: File
    ) {
        withContext(Dispatchers.IO) {
            try {
                PeriodDatabase.closeInstance()
                context.deleteDatabase("period_pal_database")
                
                if (backupFile.exists()) {
                    backupFile.copyTo(dbPath, overwrite = true)
                }
                if (backupWal.exists()) {
                    backupWal.copyTo(dbWal, overwrite = true)
                }
                if (backupShm.exists()) {
                    backupShm.copyTo(dbShm, overwrite = true)
                }
                
                PeriodDatabase.getDatabase(context, encrypted = false)
                
                val settingsRepo = SettingsRepository(context)
                val settings = settingsRepo.settings.first()
                if (settings.dbEncrypted) {
                    settingsRepo.updateSettings(settings.copy(dbEncrypted = false))
                }
                
                cleanupBackup(backupFile, backupWal, backupShm)
            } catch (e: Exception) {
                e.printStackTrace()
                PeriodDatabase.closeInstance()
                context.deleteDatabase("period_pal_database")
                PeriodDatabase.recreateDatabase(context, encrypted = false)
                cleanupBackup(backupFile, backupWal, backupShm)
            }
        }
    }
}
