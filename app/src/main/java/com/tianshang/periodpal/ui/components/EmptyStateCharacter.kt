package com.tianshang.periodpal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyStateCharacter(
    message: String,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val dark = Color(0xFF5D4037)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val w = size.width
            val h = size.height

            drawRoundRect(
                color = primary.copy(alpha = 0.2f),
                topLeft = Offset(w * 0.2f, h * 0.3f),
                size = Size(w * 0.6f, h * 0.5f),
                cornerRadius = CornerRadius(w * 0.15f, w * 0.15f)
            )

            drawCircle(
                color = dark,
                radius = w * 0.06f,
                center = Offset(w * 0.35f, h * 0.45f)
            )
            drawCircle(
                color = dark,
                radius = w * 0.06f,
                center = Offset(w * 0.65f, h * 0.45f)
            )

            drawCircle(
                color = Color.White,
                radius = w * 0.02f,
                center = Offset(w * 0.33f, h * 0.43f)
            )
            drawCircle(
                color = Color.White,
                radius = w * 0.02f,
                center = Offset(w * 0.63f, h * 0.43f)
            )

            drawCircle(
                color = primary.copy(alpha = 0.3f),
                radius = w * 0.08f,
                center = Offset(w * 0.25f, h * 0.55f)
            )
            drawCircle(
                color = primary.copy(alpha = 0.3f),
                radius = w * 0.08f,
                center = Offset(w * 0.75f, h * 0.55f)
            )

            drawArc(
                color = dark,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.35f, h * 0.5f),
                size = Size(w * 0.3f, h * 0.15f),
                style = Stroke(width = w * 0.025f, cap = StrokeCap.Round)
            )

            drawCircle(tertiary, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.2f))
            drawCircle(tertiary, radius = w * 0.03f, center = Offset(w * 0.4f, h * 0.25f))
            drawCircle(tertiary, radius = w * 0.03f, center = Offset(w * 0.6f, h * 0.25f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
