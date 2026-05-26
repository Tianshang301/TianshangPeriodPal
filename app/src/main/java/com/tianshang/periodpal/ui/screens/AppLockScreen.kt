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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PIN_MIN_LENGTH = 6
private const val PIN_MAX_LENGTH = 8
private const val MAX_ATTEMPTS_BEFORE_DELAY = 3
private const val MAX_ATTEMPTS_BEFORE_LONG_DELAY = 5
private const val MAX_ATTEMPTS_BEFORE_LOCKOUT = 10
private const val SHORT_DELAY_MS = 5000L
private const val LONG_DELAY_MS = 30000L
private const val LOCKOUT_DELAY_MS = 300000L

@Composable
fun AppLockScreen(
    onAuthenticated: () -> Unit
) {
    val context = LocalContext.current
    val biometricHelper = remember { BiometricHelper(context) }
    val encryptionManager = remember { EncryptionManager(context) }
    val scope = rememberCoroutineScope()

    val storedHash = remember { encryptionManager.getPasswordHash() }
    val isFirstTimeSetup = storedHash == null

    var showPinEntry by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var failedAttempts by remember { mutableIntStateOf(0) }
    var isLockedOut by remember { mutableStateOf(false) }
    var lockoutRemainingSeconds by remember { mutableIntStateOf(0) }

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
        } catch (_: Exception) {
            showPinEntry = true
            errorMessage = context.getString(R.string.auth_failed_generic)
        }
    }

    LaunchedEffect(isLockedOut) {
        if (isLockedOut) {
            while (lockoutRemainingSeconds > 0) {
                delay(1000L)
                lockoutRemainingSeconds--
            }
            isLockedOut = false
            failedAttempts = 0
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
                    if (newValue.length <= PIN_MAX_LENGTH && newValue.all { it.isDigit() }) {
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
                enabled = !isLockedOut,
                modifier = Modifier.fillMaxWidth()
            )

            if (isFirstTimeSetup) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { newValue ->
                        if (newValue.length <= PIN_MAX_LENGTH && newValue.all { it.isDigit() }) {
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

            if (isLockedOut) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = context.getString(R.string.lockout_message, lockoutRemainingSeconds),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
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

                    if (isLockedOut) return@Button

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
                            failedAttempts++
                            errorMessage = context.getString(R.string.invalid_pin)

                            when {
                                failedAttempts >= MAX_ATTEMPTS_BEFORE_LOCKOUT -> {
                                    isLockedOut = true
                                    lockoutRemainingSeconds = (LOCKOUT_DELAY_MS / 1000).toInt()
                                }
                                failedAttempts >= MAX_ATTEMPTS_BEFORE_LONG_DELAY -> {
                                    isLockedOut = true
                                    lockoutRemainingSeconds = (LONG_DELAY_MS / 1000).toInt()
                                }
                                failedAttempts >= MAX_ATTEMPTS_BEFORE_DELAY -> {
                                    isLockedOut = true
                                    lockoutRemainingSeconds = (SHORT_DELAY_MS / 1000).toInt()
                                }
                            }
                            pin = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = !isLockedOut
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
