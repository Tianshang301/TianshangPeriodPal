package com.tianshang.periodpal.utils

import android.content.Context
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
    
    /**
     * 将明文数据库迁移到加密数据库。
     * 操作流程：
     * 1. 备份当前明文数据库文件
     * 2. 关闭并删除当前数据库
     * 3. 使用新生成的密钥创建加密数据库
     * 4. 验证加密数据库可正常访问（执行简单查询）
     * 5. 清理备份文件并更新设置状态
     * 6. 若任何步骤失败，从备份恢复并回滚
     */
    suspend fun migrateToEncrypted(): MigrationResult = withContext(Dispatchers.IO) {
        val currentDbPath = context.getDatabasePath("period_pal_database")
        val backupFile = File(context.cacheDir, "plain_backup.db")
        
        try {
            // 1. 备份当前数据库
            if (currentDbPath.exists()) {
                currentDbPath.copyTo(backupFile, overwrite = true)
            }
            
            // 2. 关闭当前连接并删除数据库
            PeriodDatabase.closeInstance()
            context.deleteDatabase("period_pal_database")
            
            // 3. 创建新的加密数据库实例
            // 此时会触发 EncryptionKeyManager 生成新密钥
            val encryptedDb = PeriodDatabase.getDatabase(context, encrypted = true)
            
            // 4. 验证数据库可访问性
            // 执行一个轻量级查询以确保数据库正确初始化
            encryptedDb.periodRecordDao().getAllRecordsSync()
            
            // 5. 更新设置
            val settingsRepo = SettingsRepository(context)
            val settings = settingsRepo.settings.first()
            settingsRepo.updateSettings(settings.copy(dbEncrypted = true))
            
            // 6. 清理
            if (backupFile.exists()) backupFile.delete()
            
            MigrationResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            rollbackFromBackup(currentDbPath, backupFile)
            MigrationResult.Failure(e.message ?: "未知错误")
        }
    }
    
    /**
     * 从备份恢复数据库并回滚加密状态
     */
    private suspend fun rollbackFromBackup(dbPath: File, backupFile: File) {
        withContext(Dispatchers.IO) {
            try {
                // 1. 关闭损坏或失败的加密实例
                PeriodDatabase.closeInstance()
                
                // 2. 删除失败的数据库文件
                context.deleteDatabase("period_pal_database")
                
                // 3. 恢复备份
                if (backupFile.exists()) {
                    backupFile.copyTo(dbPath, overwrite = true)
                    backupFile.delete()
                }
                
                // 4. 重新打开明文数据库验证完整性
                // 这里强制 encrypted = false
                PeriodDatabase.getDatabase(context, encrypted = false)
                
                // 5. 确保设置回滚
                val settingsRepo = SettingsRepository(context)
                val settings = settingsRepo.settings.first()
                if (settings.dbEncrypted) {
                    settingsRepo.updateSettings(settings.copy(dbEncrypted = false))
                }
                Unit
                
            } catch (e: Exception) {
                e.printStackTrace()
                // 极端情况处理：尝试清理并重建空库
                PeriodDatabase.closeInstance()
                context.deleteDatabase("period_pal_database")
                PeriodDatabase.recreateDatabase(context, encrypted = false)
            }
        }
    }
}