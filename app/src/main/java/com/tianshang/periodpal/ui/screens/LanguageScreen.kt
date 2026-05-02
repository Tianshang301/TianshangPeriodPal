package com.tianshang.periodpal.ui.screens

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tianshang.periodpal.R
import com.tianshang.periodpal.viewmodel.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(context)
    )
    
    val settings by viewModel.settings.collectAsState()
    
    val languages = listOf(
        "zh" to "中文",
        "en" to "English",
        "ja" to "日本語",
        "ko" to "한국어",
        "fr" to "Français",
        "es" to "Español",
        "ar" to "العربية"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.language)) },
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
            languages.forEach { (code, name) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name)
                    RadioButton(
                        selected = settings.language == code,
                        onClick = {
                            viewModel.setLanguage(code)
                            // 同步保存到 SharedPreferences，确保 attachBaseContext 能读取
                            context.getSharedPreferences("periodpal_prefs", Context.MODE_PRIVATE)
                                .edit().putString("language", code).apply()
                            val locale = when (code) {
                                "zh" -> Locale.SIMPLIFIED_CHINESE
                                "en" -> Locale.ENGLISH
                                "ja" -> Locale.JAPANESE
                                "ko" -> Locale.KOREAN
                                "fr" -> Locale.FRENCH
                                "es" -> Locale("es")
                                "ar" -> Locale("ar")
                                else -> Locale.SIMPLIFIED_CHINESE
                            }
                            Locale.setDefault(locale)
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
                        }
                    )
                }
            }
        }
    }
}
