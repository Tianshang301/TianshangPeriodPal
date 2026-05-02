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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.ui.navigation.Screen
import kotlinx.coroutines.flow.first

private data class NavItem(val screen: Screen, val icon: ImageVector, val labelResId: Int)

private val navItems = listOf(
    NavItem(Screen.Calendar, Icons.Default.CalendarMonth, R.string.calendar_title),
    NavItem(Screen.Record, Icons.Default.Edit, R.string.record_title),
    NavItem(Screen.Analysis, Icons.Default.Analytics, R.string.analysis_title),
    NavItem(Screen.Reminder, Icons.Default.Notifications, R.string.reminder_title),
    NavItem(Screen.Profile, Icons.Default.Person, R.string.profile_title)
)

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
    
    val settings by settingsRepository.settings.collectAsState(initial = null)
    val backgroundImageUri = settings?.backgroundImageUri
    val backgroundTransparency = settings?.backgroundTransparency ?: 50
    
    Scaffold(
        topBar = topBar,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = stringResource(item.labelResId)) },
                            label = {
                                Text(
                                    stringResource(item.labelResId),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (backgroundImageUri != null) {
                AsyncImage(
                    model = backgroundImageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
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
