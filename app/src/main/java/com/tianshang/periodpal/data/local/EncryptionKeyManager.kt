package com.tianshang.periodpal.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

object EncryptionKeyManager {
    private const val PREFS_NAME = "encrypted_prefs"
    private const val PASSPHRASE_KEY = "db_passphrase"
    private const val KEY_ALIAS = "period_pal_master_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    
    fun getOrCreatePassphrase(context: Context): ByteArray {
        val masterKey = getMasterKey(context)
        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        
        val existingPassphrase = sharedPreferences.getString(PASSPHRASE_KEY, null)
        
        return if (existingPassphrase != null) {
            existingPassphrase.toByteArray(Charsets.UTF_8)
        } else {
            val newPassphrase = generateRandomPassphrase()
            sharedPreferences.edit()
                .putString(PASSPHRASE_KEY, String(newPassphrase, Charsets.UTF_8))
                .apply()
            newPassphrase
        }
    }
    
    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context, KEY_ALIAS)
            .setKeyGenParameterSpec(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
            )
            .build()
    }
    
    private fun generateRandomPassphrase(): ByteArray {
        val random = SecureRandom()
        val passphrase = ByteArray(32)
        random.nextBytes(passphrase)
        return passphrase
    }
}
