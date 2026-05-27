package com.tianshang.periodpal.ui.theme

import androidx.compose.ui.graphics.Color

object HslColorUtils {
    
    data class Hsl(val hue: Float, val saturation: Float, val lightness: Float) {
        fun toHex(): String {
            return hslToHex(hue, saturation, lightness)
        }
        
        fun toColor(): Color {
            return hslToColor(hue, saturation, lightness)
        }
    }
    
    fun hexToHsl(hex: String): Hsl {
        return try {
            val color = Color(android.graphics.Color.parseColor(hex))
            rgbToHsl(color.red, color.green, color.blue)
        } catch (_: Exception) {
            Hsl(340f, 60f, 85f) // Default pink
        }
    }
    
    fun hslToColor(h: Float, s: Float, l: Float): Color {
        val hNorm = h / 360f
        val sNorm = s / 100f
        val lNorm = l / 100f
        
        val c = (1f - kotlin.math.abs(2f * lNorm - 1f)) * sNorm
        val x = c * (1f - kotlin.math.abs((hNorm * 6f) % 2f - 1f))
        val m = lNorm - c / 2f
        
        val (r, g, b) = when ((hNorm * 6f).toInt() % 6) {
            0 -> Triple(c, x, 0f)
            1 -> Triple(x, c, 0f)
            2 -> Triple(0f, c, x)
            3 -> Triple(0f, x, c)
            4 -> Triple(x, 0f, c)
            5 -> Triple(c, 0f, x)
            else -> Triple(0f, 0f, 0f)
        }
        
        return Color((r + m).coerceIn(0f, 1f), (g + m).coerceIn(0f, 1f), (b + m).coerceIn(0f, 1f))
    }
    
    fun hslToHex(h: Float, s: Float, l: Float): String {
        val color = hslToColor(h, s, l)
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()
        return "#%02X%02X%02X".format(r, g, b)
    }
    
    private fun rgbToHsl(r: Float, g: Float, b: Float): Hsl {
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val l = (max + min) / 2f
        
        if (max == min) {
            return Hsl(0f, 0f, l * 100f)
        }
        
        val d = max - min
        val s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
        
        val h = when (max) {
            r -> ((g - b) / d + (if (g < b) 6f else 0f)) * 60f
            g -> ((b - r) / d + 2f) * 60f
            b -> ((r - g) / d + 4f) * 60f
            else -> 0f
        }
        
        return Hsl(h, s * 100f, l * 100f)
    }
}
