package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tianshang.periodpal.R
import com.tianshang.periodpal.ui.navigation.Screen
import com.tianshang.periodpal.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(context)
    )
    
    val settings by viewModel.settings.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Settings entry
        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_title)) },
            leadingContent = { Icon(Icons.Default.Settings, null) },
            modifier = Modifier.clickable {
                navController.navigate(Screen.Settings.route)
            }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Appearance section
        Text(
            text = stringResource(R.string.section_appearance),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        
        ListItem(
            headlineContent = { Text(stringResource(R.string.dark_mode)) },
            supportingContent = { Text(stringResource(R.string.dark_mode_description)) },
            leadingContent = { Icon(Icons.Default.DarkMode, null) },
            trailingContent = {
                Switch(
                    checked = settings.darkMode,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }
        )
        
        ListItem(
            headlineContent = { Text(stringResource(R.string.theme_customization)) },
            leadingContent = { Icon(Icons.Default.Palette, null) },
            modifier = Modifier.clickable {
                navController.navigate(Screen.Theme.route)
            }
        )
        
        ListItem(
            headlineContent = { Text(stringResource(R.string.language)) },
            leadingContent = { Icon(Icons.Default.Language, null) },
            modifier = Modifier.clickable {
                navController.navigate(Screen.Language.route)
            }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Data section
        Text(
            text = stringResource(R.string.section_data),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        
        ListItem(
            headlineContent = { Text(stringResource(R.string.recycle_bin)) },
            leadingContent = { Icon(Icons.Default.Delete, null) },
            modifier = Modifier.clickable {
                navController.navigate(Screen.RecycleBin.route)
            }
        )
        
        ListItem(
            headlineContent = { Text(stringResource(R.string.backup_restore)) },
            leadingContent = { Icon(Icons.Default.Backup, null) },
            modifier = Modifier.clickable {
                navController.navigate(Screen.Backup.route)
            }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        // Other section
        Text(
            text = stringResource(R.string.section_other),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        
        ListItem(
            headlineContent = { Text(stringResource(R.string.bmi_title)) },
            leadingContent = { Icon(Icons.Default.MonitorWeight, null) },
            modifier = Modifier.clickable {
                navController.navigate(Screen.Bmi.route)
            }
        )
        
        ListItem(
            headlineContent = { Text(stringResource(R.string.about)) },
            leadingContent = { Icon(Icons.Default.Info, null) },
            modifier = Modifier.clickable {
                // Show about dialog
            }
        )
    }
}
