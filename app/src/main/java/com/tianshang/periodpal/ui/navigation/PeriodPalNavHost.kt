package com.tianshang.periodpal.ui.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.ui.screens.*
import kotlinx.coroutines.flow.first

sealed class Screen(val route: String, val titleResId: Int) {
    object Terms : Screen("terms", R.string.terms_title)
    object AppLock : Screen("app_lock", R.string.app_lock_title)
    object Calendar : Screen("calendar", R.string.calendar_title)
    object Record : Screen("record", R.string.record_title)
    object RecordWithDate : Screen("record/{date}", R.string.record_title) {
        fun createRoute(date: String) = "record/$date"
    }
    object Analysis : Screen("analysis", R.string.analysis_title)
    object Reminder : Screen("reminder", R.string.reminder_title)
    object Profile : Screen("profile", R.string.profile_title)
    object RecycleBin : Screen("recycle_bin", R.string.recycle_bin_title)
    object Backup : Screen("backup", R.string.backup_restore_title)
    object Settings : Screen("settings", R.string.settings_title)
    object Language : Screen("language", R.string.language)
    object Theme : Screen("theme", R.string.theme_customization)
    object Bmi : Screen("bmi", R.string.bmi_title)
}

@Composable
fun PeriodPalNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context) }
    var isReady by remember { mutableStateOf(false) }
    var startDest by remember { mutableStateOf(Screen.Terms.route) }
    
    // 检查用户协议和应用锁状态
    LaunchedEffect(Unit) {
        val settings = settingsRepository.settings.first()
        startDest = when {
            !settings.termsAccepted -> Screen.Terms.route
            settings.appLockEnabled -> Screen.AppLock.route
            else -> Screen.Calendar.route
        }
        isReady = true
    }
    
    // 等待状态加载完成后再渲染 NavHost
    if (!isReady) return
    
    MainScreen(navController = navController) {
        BackHandler {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute == startDest || currentRoute == null) {
                (context as? Activity)?.finish()
            } else {
                navController.popBackStack()
            }
        }
        
        NavHost(
            navController = navController,
            startDestination = startDest
        ) {
            composable(Screen.Terms.route) {
                TermsScreen(
                    onTermsAccepted = {
                        navController.navigate(Screen.Calendar.route) {
                            popUpTo(Screen.Terms.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.AppLock.route) {
                AppLockScreen(
                    onAuthenticated = {
                        navController.navigate(Screen.Calendar.route) {
                            popUpTo(Screen.AppLock.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Calendar.route) {
                CalendarScreen(navController)
            }
            
            composable(Screen.Record.route) {
                RecordScreen(navController)
            }
            
            composable(Screen.RecordWithDate.route) { backStackEntry ->
                val date = backStackEntry.arguments?.getString("date")
                RecordScreen(navController, initialDate = date)
            }
            
            composable(Screen.Analysis.route) {
                AnalysisScreen(navController)
            }
            
            composable(Screen.Reminder.route) {
                ReminderScreen(navController)
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }
            
            composable(Screen.RecycleBin.route) {
                RecycleBinScreen(navController)
            }
            
            composable(Screen.Backup.route) {
                BackupScreen(navController)
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }
            composable(Screen.Language.route) {
                LanguageScreen(navController)
            }
            composable(Screen.Theme.route) {
                ThemeScreen(navController)
            }
            composable(Screen.Bmi.route) {
                BmiScreen(navController)
            }
        }
    }
}
