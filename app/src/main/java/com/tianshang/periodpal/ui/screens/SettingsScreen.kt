package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tianshang.periodpal.PeriodPalApplication
import com.tianshang.periodpal.R
import com.tianshang.periodpal.utils.DatabaseMigrationManager
import com.tianshang.periodpal.utils.EncryptionManager
import com.tianshang.periodpal.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(context)
    )
    val encryptionManager = remember { EncryptionManager(context) }
    val migrationManager = remember { DatabaseMigrationManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    val settings by viewModel.settings.collectAsState()
    
    var migrationMessage by remember { mutableStateOf<String?>(null) }
    var isMigrating by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Security settings
            Text(
                stringResource(R.string.security_settings),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // App Lock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.app_lock),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (settings.appLockEnabled) stringResource(R.string.enabled) else stringResource(R.string.disabled),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = settings.appLockEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            viewModel.toggleAppLock(true)
                        } else {
                            encryptionManager.clearPasswordHash()
                            viewModel.toggleAppLock(false)
                        }
                    }
                )
            }
            
            // App Lock Background Delay (only shown when app lock enabled)
            if (settings.appLockEnabled) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp, end = 8.dp, top = 4.dp, bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.background_lock_delay),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (settings.appLockBackgroundDelay == 0) {
                                stringResource(R.string.lock_immediately)
                            } else {
                                context.getString(R.string.lock_after_seconds, settings.appLockBackgroundDelay)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Slider(
                        value = settings.appLockBackgroundDelay.toFloat(),
                        onValueChange = { viewModel.updateBackgroundLockDelay(it.toInt()) },
                        valueRange = 0f..30f,
                        steps = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Prevent screenshot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.prevent_screenshot),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.prevent_screenshot_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = settings.preventScreenshot,
                    onCheckedChange = { viewModel.togglePreventScreenshot(it) }
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Data Security Section
            Text(
                stringResource(R.string.data_security),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Database Encryption Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.database_encryption),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (settings.dbEncrypted) stringResource(R.string.encrypted) else stringResource(R.string.unencrypted),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (settings.dbEncrypted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            if (!settings.dbEncrypted) {
                Text(
                    text = stringResource(R.string.migration_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Button(
                    onClick = {
                        isMigrating = true
                        migrationMessage = null
                        coroutineScope.launch {
                            val result = migrationManager.migrateToEncrypted()
                            isMigrating = false
                            migrationMessage = when (result) {
                                is DatabaseMigrationManager.MigrationResult.Success -> {
                                    // Reinitialize database with new encryption state
                                    (context.applicationContext as? PeriodPalApplication)?.reinitializeDatabase()
                                    context.getString(R.string.migration_success)
                                }
                                is DatabaseMigrationManager.MigrationResult.Failure -> {
                                    context.getString(R.string.migration_failed, result.reason)
                                }
                            }
                        }
                    },
                    enabled = !isMigrating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isMigrating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.migrate_to_encrypted))
                    }
                }
                
                if (migrationMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = migrationMessage!!,
                        color = if (migrationMessage!!.startsWith(context.getString(R.string.migration_success).take(4))) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
