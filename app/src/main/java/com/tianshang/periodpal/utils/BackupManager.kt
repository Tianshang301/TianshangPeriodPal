package com.tianshang.periodpal.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManager(private val context: Context) {
    
    fun exportDatabase(): Uri? {
        return try {
            val dbFile = context.getDatabasePath("period_pal_database")
            if (!dbFile.exists()) return null
            
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()
            
            val exportFile = File(exportDir, "period_pal_backup_${System.currentTimeMillis()}.zip")
            
            ZipOutputStream(FileOutputStream(exportFile)).use { zipOut ->
                // Add database file
                zipOut.putNextEntry(ZipEntry("period_pal_database"))
                FileInputStream(dbFile).use { input ->
                    input.copyTo(zipOut)
                }
                zipOut.closeEntry()
                
                // Add hash for integrity verification
                val hash = calculateFileHash(dbFile)
                zipOut.putNextEntry(ZipEntry("integrity_hash.txt"))
                zipOut.write(hash.toByteArray())
                zipOut.closeEntry()
            }
            
            Uri.fromFile(exportFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun importDatabase(uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val tempDir = File(context.cacheDir, "import_temp")
                tempDir.mkdirs()
                
                ZipInputStream(inputStream).use { zipIn ->
                    var entry: ZipEntry?
                    var hash: String? = null
                    
                    while (zipIn.nextEntry.also { entry = it } != null) {
                        val file = File(tempDir, entry!!.name)
                        
                        if (entry!!.name == "integrity_hash.txt") {
                            hash = zipIn.bufferedReader().readText()
                        } else {
                            FileOutputStream(file).use { output ->
                                zipIn.copyTo(output)
                            }
                        }
                    }
                    
                    // Verify integrity
                    val dbFile = File(tempDir, "period_pal_database")
                    if (dbFile.exists() && hash != null) {
                        val calculatedHash = calculateFileHash(dbFile)
                        if (calculatedHash == hash) {
                            // Replace current database
                            val currentDb = context.getDatabasePath("period_pal_database")
                            dbFile.copyTo(currentDb, overwrite = true)
                            return true
                        }
                    }
                }
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun calculateFileHash(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } > 0) {
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
