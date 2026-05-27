package com.tianshang.periodpal.utils

import android.content.Context
import android.net.Uri
import com.tianshang.periodpal.data.local.EncryptionKeyManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class BackupManager(private val context: Context) {
    
    companion object {
        private const val METADATA_FILE = "backup_metadata.txt"
        private const val DB_FILE = "period_pal_database"
        private const val HASH_FILE = "integrity_hash.txt"
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
        private const val MAGIC_ENCRYPTED = "ENCRYPTED_V1"
    }
    
    fun exportDatabase(): Uri? {
        return try {
            val dbFile = context.getDatabasePath("period_pal_database")
            if (!dbFile.exists()) return null
            
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()
            
            val exportFile = File(exportDir, "period_pal_backup_${System.currentTimeMillis()}.zip")
            
            val dbHash = calculateFileHash(dbFile)
            val passphrase = EncryptionKeyManager.getOrCreatePassphrase(context)
            val secretKey = deriveKey(passphrase)
            
            ZipOutputStream(FileOutputStream(exportFile)).use { zipOut ->
                // Metadata indicating encrypted format
                zipOut.putNextEntry(ZipEntry(METADATA_FILE))
                zipOut.write(MAGIC_ENCRYPTED.toByteArray())
                zipOut.closeEntry()
                
                // Encrypt and add database file
                val iv = ByteArray(GCM_IV_LENGTH)
                SecureRandom().nextBytes(iv)
                
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
                
                zipOut.putNextEntry(ZipEntry(DB_FILE))
                // Write IV first
                zipOut.write(iv)
                
                FileInputStream(dbFile).use { input ->
                    CipherOutputStream(zipOut, cipher).use { cipherOut ->
                        input.copyTo(cipherOut)
                    }
                }
                zipOut.closeEntry()
                
                // Hash of the ORIGINAL (unencrypted) database
                zipOut.putNextEntry(ZipEntry(HASH_FILE))
                zipOut.write(dbHash.toByteArray())
                zipOut.closeEntry()
            }
            
            Uri.fromFile(exportFile)
        } catch (_: Exception) {
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
                    var isEncrypted = false
                    var dbBytes: ByteArray? = null

                    while (zipIn.nextEntry.also { entry = it } != null) {
                        val entryName = entry!!.name
                        val sanitizedName = File(entryName).name
                        if (sanitizedName != entryName || entryName.contains("..") || entryName.contains("/")) {
                            zipIn.closeEntry()
                            continue
                        }

                        when (sanitizedName) {
                            METADATA_FILE -> {
                                val content = zipIn.bufferedReader().readText()
                                isEncrypted = content.contains(MAGIC_ENCRYPTED)
                            }
                            HASH_FILE -> {
                                hash = zipIn.bufferedReader().readText()
                            }
                            DB_FILE -> {
                                if (isEncrypted) {
                                    // Read IV (first 12 bytes)
                                    val iv = ByteArray(GCM_IV_LENGTH)
                                    var read = 0
                                    while (read < GCM_IV_LENGTH) {
                                        val n = zipIn.read(iv, read, GCM_IV_LENGTH - read)
                                        if (n == -1) break
                                        read += n
                                    }
                                    
                                    val passphrase = EncryptionKeyManager.getOrCreatePassphrase(context)
                                    val secretKey = deriveKey(passphrase)
                                    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                                    val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
                                    cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
                                    
                                    val bos = ByteArrayOutputStream()
                                    CipherInputStream(zipIn, cipher).use { cis ->
                                        cis.copyTo(bos)
                                    }
                                    dbBytes = bos.toByteArray()
                                } else {
                                    // Legacy plaintext format
                                    val file = File(tempDir, sanitizedName)
                                    FileOutputStream(file).use { output ->
                                        zipIn.copyTo(output)
                                    }
                                }
                            }
                        }
                    }

                    val dbFile = if (isEncrypted && dbBytes != null) {
                        val file = File(tempDir, "period_pal_database")
                        file.writeBytes(dbBytes)
                        file
                    } else {
                        File(tempDir, "period_pal_database")
                    }
                    
                    if (dbFile.exists() && hash != null) {
                        val calculatedHash = calculateFileHash(dbFile)
                        if (calculatedHash == hash) {
                            val currentDb = context.getDatabasePath("period_pal_database")
                            dbFile.copyTo(currentDb, overwrite = true)
                            return true
                        }
                    }
                }
            }
            false
        } catch (_: Exception) {
            false
        }
    }
    
    private fun deriveKey(passphrase: ByteArray): SecretKey {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(passphrase)
        return SecretKeySpec(keyBytes, "AES")
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
