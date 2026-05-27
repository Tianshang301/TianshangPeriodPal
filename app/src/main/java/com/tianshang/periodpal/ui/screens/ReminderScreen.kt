package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.model.CustomReminder
import com.tianshang.periodpal.viewmodel.ReminderViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModel.Factory(context)
    )
    
    val settings by viewModel.settings.collectAsState()
    val customReminders by viewModel.customReminders.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Custom Reminders Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.custom_reminders),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_custom_reminder),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (customReminders.isEmpty()) {
            Text(
                text = stringResource(R.string.no_custom_reminders),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            customReminders.forEach { reminder ->
                CustomReminderCard(
                    reminder = reminder,
                    onToggle = { viewModel.toggleCustomReminder(reminder.id, it) },
                    onDelete = { viewModel.removeCustomReminder(reminder.id) }
                )
            }
        }
    }
    
    if (showAddDialog) {
        AddCustomReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, message, dateTime ->
                viewModel.addCustomReminder(
                    CustomReminder.fromLocalDateTime(title, message, dateTime)
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CustomReminderCard(
    reminder: CustomReminder,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (reminder.message.isNotEmpty()) {
                    Text(
                        text = reminder.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = reminder.getLocalDateTime().format(formatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = reminder.enabled,
                    onCheckedChange = onToggle
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, message: String, dateTime: LocalDateTime) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now().plusHours(1)) }
    var titleError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_custom_reminder)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text(stringResource(R.string.reminder_title_label)) },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text(stringResource(R.string.reminder_title_required)) }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text(stringResource(R.string.reminder_message_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date picker trigger
                OutlinedTextField(
                    value = selectedDate.format(dateFormatter),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.reminder_date)) },
                    readOnly = true,
                    trailingIcon = {
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(stringResource(R.string.select_date))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Time picker trigger
                OutlinedTextField(
                    value = selectedTime.format(timeFormatter),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.reminder_time)) },
                    readOnly = true,
                    trailingIcon = {
                        TextButton(onClick = { showTimePicker = true }) {
                            Text(stringResource(R.string.select_time))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@TextButton
                    }
                    val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                    onConfirm(title.trim(), message.trim(), dateTime)
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * 86400000L
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = LocalDate.ofEpochDay(millis / 86400000L)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showTimePicker) {
        WheelTimePicker(
            startTime = selectedTime,
            minTime = LocalTime.of(0, 0),
            maxTime = LocalTime.of(23, 59),
            timeFormat = TimeFormat.HOUR_24,
            size = DpSize(200.dp, 180.dp),
            rowCount = 5,
            textStyle = MaterialTheme.typography.titleMedium,
            textColor = MaterialTheme.colorScheme.onSurface,
            selectorProperties = com.commandiron.wheel_picker_compose.core.WheelPickerDefaults.selectorProperties(
                enabled = true,
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            )
        ) { snappedTime ->
            selectedTime = snappedTime
            showTimePicker = false
        }
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
    val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
    val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
    
    var useWheelPicker by remember { mutableStateOf(false) }
    var timeText by remember(time) { mutableStateOf(time) }
    var timeError by remember { mutableStateOf(false) }
    
    fun parseAndApplyTime(text: String) {
        val regex = Regex("^\\d{2}:\\d{2}$")
        if (!regex.matches(text)) {
            timeError = true
            return
        }
        val parts = text.split(":")
        val h = parts[0].toIntOrNull() ?: -1
        val m = parts[1].toIntOrNull() ?: -1
        if (h !in 0..23 || m !in 0..59) {
            timeError = true
            return
        }
        timeError = false
        onTimeChange(text)
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
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.reminder_time),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { useWheelPicker = !useWheelPicker }) {
                        Icon(
                            imageVector = if (useWheelPicker) Icons.Default.Keyboard else Icons.Default.Schedule,
                            contentDescription = stringResource(
                                if (useWheelPicker) R.string.time_picker_mode_keyboard
                                else R.string.time_picker_mode_wheel
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (useWheelPicker) {
                    WheelTimePicker(
                        startTime = LocalTime.of(initialHour, initialMinute),
                        minTime = LocalTime.of(0, 0),
                        maxTime = LocalTime.of(23, 59),
                        timeFormat = TimeFormat.HOUR_24,
                        size = DpSize(200.dp, 180.dp),
                        rowCount = 5,
                        textStyle = MaterialTheme.typography.titleMedium,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        selectorProperties = com.commandiron.wheel_picker_compose.core.WheelPickerDefaults.selectorProperties(
                            enabled = true,
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        )
                    ) { snappedTime ->
                        val newTime = "%02d:%02d".format(snappedTime.hour, snappedTime.minute)
                        onTimeChange(newTime)
                        timeText = newTime
                    }
                } else {
                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { newValue ->
                            if (newValue.length <= 5) {
                                timeText = newValue
                                timeError = false
                            }
                        },
                        label = { Text("HH:MM") },
                        isError = timeError,
                        supportingText = if (timeError) {
                            { Text(stringResource(R.string.time_format_error)) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Button(
                        onClick = { parseAndApplyTime(timeText) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}
