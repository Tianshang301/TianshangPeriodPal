package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
        // Period reminder
        ReminderCard(
            title = stringResource(R.string.period_reminder),
            enabled = settings.periodReminderEnabled,
            days = settings.periodReminderDays,
            time = settings.periodReminderTime,
            onEnabledChange = { viewModel.updatePeriodReminderEnabled(it) },
            onDaysChange = { viewModel.updatePeriodReminderDays(it) },
            onTimeChange = { viewModel.updatePeriodReminderTime(it) }
        )
        
        // Ovulation reminder
        ReminderCard(
            title = stringResource(R.string.ovulation_reminder),
            enabled = settings.ovulationReminderEnabled,
            days = settings.ovulationReminderDays,
            time = settings.periodReminderTime,
            onEnabledChange = { viewModel.updateOvulationReminderEnabled(it) },
            onDaysChange = { viewModel.updateOvulationReminderDays(it) },
            onTimeChange = { viewModel.updatePeriodReminderTime(it) }
        )
        
        // PMS reminder
        ReminderCard(
            title = stringResource(R.string.pms_reminder),
            enabled = settings.pmsReminderEnabled,
            days = settings.pmsReminderDays,
            time = settings.periodReminderTime,
            onEnabledChange = { viewModel.updatePmsReminderEnabled(it) },
            onDaysChange = { viewModel.updatePmsReminderDays(it) },
            onTimeChange = { viewModel.updatePeriodReminderTime(it) }
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("提前天数")
                    Slider(
                        value = days.toFloat(),
                        onValueChange = { onDaysChange(it.toInt()) },
                        valueRange = 0f..7f,
                        steps = 6,
                        modifier = Modifier.width(200.dp)
                    )
                    Text("${days}天")
                }
                
                OutlinedTextField(
                    value = time,
                    onValueChange = onTimeChange,
                    label = { Text("提醒时间") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
