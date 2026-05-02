package com.tianshang.periodpal.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BiometricHelper(private val context: Context) {
    
    private val biometricManager = BiometricManager.from(context)
    
    fun isBiometricAvailable(): Boolean {
        return try {
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            ) == BiometricManager.BIOMETRIC_SUCCESS
        } catch (e: Exception) {
            false
        }
    }
    
    fun authenticate(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val activity = context as? FragmentActivity
            if (activity == null) {
                onError("无法启动生物识别")
                return
            }
            
            val executor = ContextCompat.getMainExecutor(context)
            
            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }
                    
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onError("验证失败")
                    }
                    
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        onError(errString.toString())
                    }
                })
            
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("应用锁")
                .setSubtitle("请验证身份以继续")
                .setNegativeButtonText("使用密码")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()
            
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError("生物识别初始化失败: ${e.message}")
        }
    }
    
    companion object {
        private const val KEY_NAME = "period_pal_biometric_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        
        fun generateSecretKey(): SecretKey {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            if (!keyStore.containsAlias(KEY_NAME)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
                )
                
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(
                        KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setUserAuthenticationRequired(true)
                        .setInvalidatedByBiometricEnrollment(true)
                        .build()
                )
                
                return keyGenerator.generateKey()
            }
            
            return keyStore.getKey(KEY_NAME, null) as SecretKey
        }
        
        fun getCipher(): Cipher {
            return Cipher.getInstance(
                "${KeyProperties.KEY_ALGORITHM_AES}/" +
                "${KeyProperties.BLOCK_MODE_CBC}/" +
                KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        }
    }
}
