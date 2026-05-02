package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.tianshang.periodpal.R
import com.tianshang.periodpal.utils.BiometricHelper
import com.tianshang.periodpal.utils.EncryptionManager

private const val PIN_MIN_LENGTH = 4
private const val PIN_MAX_LENGTH = 8

@Composable
fun AppLockScreen(
    onAuthenticated: () -> Unit
) {
    val context = LocalContext.current
    val biometricHelper = remember { BiometricHelper(context) }
    val encryptionManager = remember { EncryptionManager(context) }
    
    val storedHash = remember { encryptionManager.getPasswordHash() }
    val isFirstTimeSetup = storedHash == null
    
    var showPinEntry by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        try {
            if (!isFirstTimeSetup && biometricHelper.isBiometricAvailable()) {
                biometricHelper.authenticate(
                    onSuccess = onAuthenticated,
                    onError = { error ->
                        showPinEntry = true
                        errorMessage = error
                    }
                )
            } else {
                showPinEntry = true
            }
        } catch (e: Exception) {
            showPinEntry = true
            errorMessage = context.getString(R.string.auth_failed, e.message ?: "")
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isFirstTimeSetup) {
                stringResource(R.string.app_lock_setup_title)
            } else {
                stringResource(R.string.app_lock_title)
            },
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        if (showPinEntry) {
            OutlinedTextField(
                value = pin,
                onValueChange = { newValue ->
                    if (newValue.length <= PIN_MAX_LENGTH) {
                        pin = newValue
                    }
                },
                label = { 
                    Text(
                        if (isFirstTimeSetup) {
                            stringResource(R.string.set_pin)
                        } else {
                            stringResource(R.string.enter_pin)
                        }
                    ) 
                },
                supportingText = {
                    if (isFirstTimeSetup) {
                        Text("${pin.length}/$PIN_MAX_LENGTH")
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (isFirstTimeSetup) {
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { newValue ->
                        if (newValue.length <= PIN_MAX_LENGTH) {
                            confirmPin = newValue
                        }
                    },
                    label = { Text(stringResource(R.string.confirm_pin)) },
                    supportingText = {
                        Text("${confirmPin.length}/$PIN_MAX_LENGTH")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Button(
                onClick = {
                    errorMessage = ""
                    
                    if (isFirstTimeSetup) {
                        if (pin.length < PIN_MIN_LENGTH) {
                            errorMessage = context.getString(R.string.pin_length_error, PIN_MIN_LENGTH)
                            return@Button
                        }
                        if (pin != confirmPin) {
                            errorMessage = context.getString(R.string.pin_mismatch_error)
                            return@Button
                        }
                        val hash = encryptionManager.hashPassword(pin)
                        encryptionManager.savePasswordHash(hash)
                        onAuthenticated()
                    } else {
                        if (encryptionManager.verifyPassword(pin, storedHash!!)) {
                            onAuthenticated()
                        } else {
                            errorMessage = context.getString(R.string.invalid_pin)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    if (isFirstTimeSetup) {
                        stringResource(R.string.set_password)
                    } else {
                        stringResource(R.string.unlock)
                    }
                )
            }
        }
    }
}
