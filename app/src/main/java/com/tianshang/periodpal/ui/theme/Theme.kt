package com.tianshang.periodpal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    secondary = PinkSecondary,
    tertiary = PinkTertiary,
    background = PinkBackground,
    surface = PinkSurface,
    onPrimary = OnPinkPrimary,
    onSecondary = OnPinkSecondary,
    onBackground = OnPinkBackground,
    onSurface = OnPinkSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = PinkPrimaryDark,
    secondary = PinkSecondaryDark,
    tertiary = PinkTertiaryDark,
    background = PinkBackgroundDark,
    surface = PinkSurfaceDark,
    onPrimary = OnPinkPrimaryDark,
    onSecondary = OnPinkSecondaryDark,
    onBackground = OnPinkBackgroundDark,
    onSurface = OnPinkSurfaceDark
)

@Composable
fun PeriodPalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themeColor: String? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    val colorScheme = when {
        themeColor != null -> {
            // 使用自定义主题颜色生成 ColorScheme
            val seedColor = try {
                Color(android.graphics.Color.parseColor(themeColor))
            } catch (e: Exception) {
                PinkPrimary
            }
            
            // 使用 Material3 的 fromSeed 方法生成完整 ColorScheme
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // API 31+ 使用 dynamic color 但覆盖 primary
                val dynamicScheme = if (darkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
                dynamicScheme.copy(
                    primary = seedColor,
                    onPrimary = if (darkTheme) Color.White else Color.Black,
                    primaryContainer = seedColor.copy(alpha = 0.2f),
                    onPrimaryContainer = seedColor
                )
            } else {
                // API 31 以下，使用 MaterialTheme.colorScheme 的扩展方法
                generateColorSchemeFromSeed(seedColor, darkTheme)
            }
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun generateColorSchemeFromSeed(seedColor: Color, darkTheme: Boolean): ColorScheme {
    // 基于种子颜色生成完整的 ColorScheme
    val primary = seedColor
    val primaryContainer = seedColor.copy(alpha = if (darkTheme) 0.3f else 0.2f)
    val onPrimary = if (darkTheme) Color.White else Color.Black
    val onPrimaryContainer = if (darkTheme) seedColor.copy(alpha = 0.8f) else seedColor.copy(alpha = 0.6f)
    
    // 生成次要颜色（互补色或类似色）
    val secondary = seedColor.copy(
        red = (seedColor.red + 0.1f).coerceIn(0f, 1f),
        green = (seedColor.green - 0.05f).coerceIn(0f, 1f),
        blue = (seedColor.blue + 0.15f).coerceIn(0f, 1f)
    )
    
    // 生成第三颜色
    val tertiary = seedColor.copy(
        red = (seedColor.red - 0.05f).coerceIn(0f, 1f),
        green = (seedColor.green + 0.1f).coerceIn(0f, 1f),
        blue = (seedColor.blue - 0.05f).coerceIn(0f, 1f)
    )
    
    val background = if (darkTheme) Color(0xFF1C1B1F) else Color(0xFFFFFBFE)
    val surface = if (darkTheme) Color(0xFF1C1B1F) else Color(0xFFFFFBFE)
    val onBackground = if (darkTheme) Color(0xFFE3E0E6) else Color(0xFF1C1B1F)
    val onSurface = if (darkTheme) Color(0xFFE3E0E6) else Color(0xFF1C1B1F)
    
    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.3f),
            onSecondaryContainer = secondary.copy(alpha = 0.8f),
            tertiary = tertiary,
            onTertiary = Color.White,
            tertiaryContainer = tertiary.copy(alpha = 0.3f),
            onTertiaryContainer = tertiary.copy(alpha = 0.8f),
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = Color(0xFF938F99),
            outlineVariant = Color(0xFF49454F),
            scrim = Color.Black,
            inverseSurface = Color(0xFFE3E0E6),
            inverseOnSurface = Color(0xFF1C1B1F),
            inversePrimary = seedColor.copy(alpha = 0.8f),
            surfaceTint = primary
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.2f),
            onSecondaryContainer = secondary.copy(alpha = 0.6f),
            tertiary = tertiary,
            onTertiary = Color.White,
            tertiaryContainer = tertiary.copy(alpha = 0.2f),
            onTertiaryContainer = tertiary.copy(alpha = 0.6f),
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = Color(0xFFE7E0EC),
            onSurfaceVariant = Color(0xFF49454F),
            outline = Color(0xFF79747E),
            outlineVariant = Color(0xFFCAC4D0),
            scrim = Color.Black,
            inverseSurface = Color(0xFF322F35),
            inverseOnSurface = Color(0xFFF5EFF7),
            inversePrimary = seedColor.copy(alpha = 0.8f),
            surfaceTint = primary
        )
    }
}
