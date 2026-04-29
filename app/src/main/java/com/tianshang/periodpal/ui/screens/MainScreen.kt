package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.ui.navigation.Screen
import kotlinx.coroutines.flow.first

@Composable
fun MainScreen(
    navController: NavController,
    showBottomBar: Boolean = true,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // 获取背景设置
    val settings by settingsRepository.settings.collectAsState(initial = null)
    val backgroundImageUri = settings?.backgroundImageUri
    val backgroundTransparency = settings?.backgroundTransparency ?: 50
    
    val items = listOf(
        Screen.Calendar to Icons.Default.CalendarMonth,
        Screen.Record to Icons.Default.Edit,
        Screen.Analysis to Icons.Default.Analytics,
        Screen.Reminder to Icons.Default.Notifications,
        Screen.Profile to Icons.Default.Person
    )
    
    Scaffold(
        topBar = topBar,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { (screen, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // 背景图片
            if (backgroundImageUri != null) {
                AsyncImage(
                    model = backgroundImageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // 背景透明度遮罩
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface.copy(
                                alpha = 1f - (backgroundTransparency / 100f)
                            )
                        )
                )
            }
            
            content(paddingValues)
        }
    }
}
