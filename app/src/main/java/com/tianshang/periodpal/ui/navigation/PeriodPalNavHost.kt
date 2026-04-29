package com.tianshang.periodpal.ui.navigation

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
import com.tianshang.periodpal.data.repository.SettingsRepository
import com.tianshang.periodpal.ui.screens.*
import kotlinx.coroutines.flow.first

sealed class Screen(val route: String, val title: String) {
    object Terms : Screen("terms", "用户协议")
    object AppLock : Screen("app_lock", "应用锁")
    object Calendar : Screen("calendar", "日历")
    object Record : Screen("record", "记录")
    object RecordWithDate : Screen("record/{date}", "记录") {
        fun createRoute(date: String) = "record/$date"
    }
    object Analysis : Screen("analysis", "分析")
    object Reminder : Screen("reminder", "提醒")
    object Profile : Screen("profile", "我的")
    object RecycleBin : Screen("recycle_bin", "回收站")
    object Backup : Screen("backup", "备份恢复")
    object Settings : Screen("settings", "设置")
    object Language : Screen("language", "语言")
    object Theme : Screen("theme", "主题")
    object Bmi : Screen("bmi", "BMI")
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
