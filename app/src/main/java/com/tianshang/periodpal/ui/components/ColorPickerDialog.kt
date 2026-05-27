package com.tianshang.periodpal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tianshang.periodpal.R
import com.tianshang.periodpal.ui.theme.ExtraLargeShape

@Composable
fun ColorPickerDialog(
    initialColor: String,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialArgb = try {
        android.graphics.Color.parseColor(initialColor)
    } catch (_: Exception) {
        android.graphics.Color.parseColor("#FFB6C1")
    }
    val initialR = android.graphics.Color.red(initialArgb) / 255f
    val initialG = android.graphics.Color.green(initialArgb) / 255f
    val initialB = android.graphics.Color.blue(initialArgb) / 255f
    val initialHsl = rgbToHsl(initialR, initialG, initialB)

    var hue by remember { mutableStateOf(initialHsl[0]) }
    var saturation by remember { mutableStateOf(initialHsl[1] * 100f) }
    var lightness by remember { mutableStateOf(initialHsl[2] * 100f) }

    val currentColor = Color.hsl(
        hue = hue,
        saturation = (saturation / 100f).coerceIn(0f, 1f),
        lightness = (lightness / 100f).coerceIn(0f, 1f)
    )

    val hexString = remember(currentColor) {
        String.format("#%06X", 0xFFFFFF and currentColor.toArgb())
    }

    val textColor = if (lightness > 50f) Color.Black else Color.White

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.custom_color),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Color preview
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(currentColor)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.color_preview),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hex display
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = hexString,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Hue slider
                SliderLabel(
                    label = stringResource(R.string.hue),
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..360f,
                    steps = 0
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Saturation slider
                SliderLabel(
                    label = stringResource(R.string.saturation),
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = 0f..100f,
                    steps = 0
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lightness slider
                SliderLabel(
                    label = stringResource(R.string.lightness),
                    value = lightness,
                    onValueChange = { lightness = it },
                    valueRange = 0f..100f,
                    steps = 0
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = ExtraLargeShape
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            onColorSelected(hexString)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = ExtraLargeShape
                    ) {
                        Text(stringResource(R.string.apply))
                    }
                }
            }
        }
    }
}

private fun rgbToHsl(r: Float, g: Float, b: Float): FloatArray {
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    var h = 0f
    var s = 0f
    val l = (max + min) / 2f

    if (delta != 0f) {
        s = if (l <= 0.5f) delta / (max + min) else delta / (2f - max - min)
        h = when (max) {
            r -> ((g - b) / delta + (if (g < b) 6f else 0f)) * 60f
            g -> ((b - r) / delta + 2f) * 60f
            else -> ((r - g) / delta + 4f) * 60f
        }
    }
    if (h < 0f) h += 360f

    return floatArrayOf(h, s, l)
}

@Composable
private fun SliderLabel(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value.toInt().toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
