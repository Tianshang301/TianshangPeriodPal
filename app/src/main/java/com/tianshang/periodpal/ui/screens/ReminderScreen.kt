package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tianshang.periodpal.R
import com.tianshang.periodpal.viewmodel.ReminderViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModel.Factory(context)
    )
    
    val settings by viewModel.settings.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ReminderCard(
            title = stringResource(R.string.period_reminder),
            enabled = settings.periodReminderEnabled,
            days = settings.periodReminderDays,
            time = settings.periodReminderTime,
            onEnabledChange = { viewModel.updatePeriodReminderEnabled(it) },
            onDaysChange = { viewModel.updatePeriodReminderDays(it) },
            onTimeChange = { viewModel.updatePeriodReminderTime(it) }
        )
        
        ReminderCard(
            title = stringResource(R.string.ovulation_reminder),
            enabled = settings.ovulationReminderEnabled,
            days = settings.ovulationReminderDays,
            time = settings.ovulationReminderTime,
            onEnabledChange = { viewModel.updateOvulationReminderEnabled(it) },
            onDaysChange = { viewModel.updateOvulationReminderDays(it) },
            onTimeChange = { viewModel.updateOvulationReminderTime(it) }
        )
        
        ReminderCard(
            title = stringResource(R.string.pms_reminder),
            enabled = settings.pmsReminderEnabled,
            days = settings.pmsReminderDays,
            time = settings.pmsReminderTime,
            onEnabledChange = { viewModel.updatePmsReminderEnabled(it) },
            onDaysChange = { viewModel.updatePmsReminderDays(it) },
            onTimeChange = { viewModel.updatePmsReminderTime(it) }
        )
    }
}

@Composable
fun ReminderCard(
    title: String,
    enabled: Boolean,
    days: Int,
    time: String,
    onEnabledChange: (Boolean) -> Unit,
    onDaysChange: (Int) -> Unit,
    onTimeChange: (String) -> Unit
) {
    val timeParts = time.split(":")
    var hour by remember(time) { mutableFloatStateOf(timeParts.getOrNull(0)?.toFloatOrNull() ?: 8f) }
    var minute by remember(time) { mutableFloatStateOf(timeParts.getOrNull(1)?.toFloatOrNull() ?: 0f) }
    
    LaunchedEffect(hour, minute) {
        val newTime = "%02d:%02d".format(hour.toInt(), minute.toInt())
        if (newTime != time) {
            onTimeChange(newTime)
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange
                )
            }
            
            if (enabled) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.reminder_days_before),
                        modifier = Modifier.weight(0.3f),
                        maxLines = 1
                    )
                    Slider(
                        value = days.toFloat(),
                        onValueChange = { onDaysChange(it.toInt()) },
                        valueRange = 0f..7f,
                        steps = 6,
                        modifier = Modifier.weight(0.5f)
                    )
                    Text(
                        LocalContext.current.resources.getQuantityString(R.plurals.days_count, days, days),
                        modifier = Modifier.weight(0.2f),
                        maxLines = 1
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    stringResource(R.string.reminder_time),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.reminder_hour),
                        modifier = Modifier.weight(0.2f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Slider(
                        value = hour,
                        onValueChange = { hour = it },
                        valueRange = 0f..23f,
                        steps = 22,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        "%02d".format(hour.toInt()),
                        modifier = Modifier.weight(0.2f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.reminder_minute),
                        modifier = Modifier.weight(0.2f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Slider(
                        value = minute,
                        onValueChange = { minute = it },
                        valueRange = 0f..55f,
                        steps = 10,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        "%02d".format(minute.toInt()),
                        modifier = Modifier.weight(0.2f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Text(
                    text = "%02d:%02d".format(hour.toInt(), minute.toInt()),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
