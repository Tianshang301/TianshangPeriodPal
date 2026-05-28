package com.tianshang.periodpal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun CartoonCalendarDay(
    date: LocalDate,
    isPeriod: Boolean,
    isPredicted: Boolean,
    isOvulation: Boolean,
    isFertile: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = when {
        isOvulation -> Color(0xFF2196F3)
        else -> Color.Transparent
    }
    val borderWidth = if (isOvulation) 2.5.dp else 0.dp

    val backgroundColor = when {
        isPeriod -> MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
        isPredicted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        isFertile -> Color(0xFF90CAF9).copy(alpha = 0.3f)
        isOvulation -> Color(0xFF64B5F6).copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        isPeriod -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(borderWidth, borderColor, RoundedCornerShape(16.dp))
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isPeriod) FontWeight.Bold else FontWeight.Normal
                ),
                color = textColor,
                textAlign = TextAlign.Center
            )

            when {
                isPeriod -> {
                    Text(
                        text = "\uD83E\uDE78",
                        fontSize = 8.sp
                    )
                }
                isOvulation -> {
                    Text(
                        text = "\u2728",
                        fontSize = 8.sp
                    )
                }
                isPredicted -> {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    )
                }
                isToday -> {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}
