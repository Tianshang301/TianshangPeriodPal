package com.tianshang.periodpal.utils

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

class EncryptionManager(private val context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun hashPassword(password: String): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        val parameters = Argon2Parameters.Builder()
            .withSalt(salt)
            .withParallelism(1)
            .withMemoryAsKB(65536)
            .withIterations(3)
            .build()

        val generator = Argon2BytesGenerator()
        generator.init(parameters)

        val passwordBytes = password.toByteArray(StandardCharsets.UTF_8)
        val result = ByteArray(32)
        generator.generateBytes(passwordBytes, result)

        val saltBase64 = Base64.encodeToString(salt, Base64.DEFAULT)
        val hashBase64 = Base64.encodeToString(result, Base64.DEFAULT)

        return "$saltBase64:$hashBase64"
    }
    
    fun verifyPassword(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false

        val salt = Base64.decode(parts[0], Base64.DEFAULT)
        val expectedHash = Base64.decode(parts[1], Base64.DEFAULT)

        val parameters = Argon2Parameters.Builder()
            .withSalt(salt)
            .withParallelism(1)
            .withMemoryAsKB(65536)
            .withIterations(3)
            .build()

        val generator = Argon2BytesGenerator()
        generator.init(parameters)

        val passwordBytes = password.toByteArray(StandardCharsets.UTF_8)
        val result = ByteArray(32)
        generator.generateBytes(passwordBytes, result)

        return result.contentEquals(expectedHash)
    }
    
    fun savePasswordHash(hash: String) {
        encryptedPrefs.edit().putString("app_lock_password", hash).apply()
    }
    
    fun getPasswordHash(): String? {
        return encryptedPrefs.getString("app_lock_password", null)
    }
    
    fun clearPasswordHash() {
        encryptedPrefs.edit().remove("app_lock_password").apply()
    }
}
