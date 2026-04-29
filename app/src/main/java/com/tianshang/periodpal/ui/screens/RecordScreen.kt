package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tianshang.periodpal.R
import com.tianshang.periodpal.viewmodel.RecordViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecordScreen(
    navController: NavController,
    initialDate: String? = null
) {
    val context = LocalContext.current
    val viewModel: RecordViewModel = viewModel(
        factory = RecordViewModel.Factory(context)
    )
    
    // 解析初始日期，默认为今日
    val initialLocalDate = remember {
        initialDate?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                LocalDate.now()
            }
        } ?: LocalDate.now()
    }
    
    var selectedDate by remember { mutableStateOf(initialLocalDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isPeriodStart by remember { mutableStateOf(false) }
    var isPeriodEnd by remember { mutableStateOf(false) }
    var flowLevel by remember { mutableStateOf(0) }
    var painLevel by remember { mutableStateOf(0) }
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var hasSexualActivity by remember { mutableStateOf(false) }
    var ovulationTest by remember { mutableStateOf("") }
    var cervicalMucus by remember { mutableStateOf("") }
    var bodyTemperature by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    val symptoms = listOf(
        "头痛", "腹胀", "乳房胀痛", "腰痛", 
        "疲劳", "情绪波动", "痤疮", "食欲增加", "失眠", "恶心"
    )
    
    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_record)) },
            text = { Text(stringResource(R.string.delete_record_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecord(selectedDate)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    // Material3 DatePicker 状态
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 日期选择器
        OutlinedCard(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.record_date),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = selectedDate.format(dateFormatter),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.select_date),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { 
                    isPeriodStart = true
                    viewModel.quickStartPeriod(selectedDate)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.start_period))
            }
            
            Button(
                onClick = { 
                    isPeriodEnd = true
                    viewModel.quickEndPeriod(selectedDate)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(R.string.end_period))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Flow level
        Text(stringResource(R.string.flow_level), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(1 to "轻", 2 to "中", 3 to "重").forEach { (level, label) ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = flowLevel == level,
                            onClick = { flowLevel = level },
                            role = Role.RadioButton
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = flowLevel == level,
                        onClick = null
                    )
                    Text(label, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
        
        // Pain level
        Text(stringResource(R.string.pain_level), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(0 to "无", 1 to "轻微", 2 to "中度", 3 to "重度").forEach { (level, label) ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = painLevel == level,
                            onClick = { painLevel = level },
                            role = Role.RadioButton
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = painLevel == level,
                        onClick = null
                    )
                    Text(label, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
        
        // Symptoms
        Text(stringResource(R.string.symptoms), style = MaterialTheme.typography.titleMedium)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            symptoms.forEach { symptom ->
                FilterChip(
                    selected = selectedSymptoms.contains(symptom),
                    onClick = {
                        selectedSymptoms = if (selectedSymptoms.contains(symptom)) {
                            selectedSymptoms - symptom
                        } else {
                            selectedSymptoms + symptom
                        }
                    },
                    label = { Text(symptom) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        
        // Sexual activity
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = hasSexualActivity,
                onCheckedChange = { hasSexualActivity = it }
            )
            Text(stringResource(R.string.sexual_activity))
        }
        
        // Ovulation test
        OutlinedTextField(
            value = ovulationTest,
            onValueChange = { ovulationTest = it },
            label = { Text(stringResource(R.string.ovulation_test)) },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Cervical mucus
        OutlinedTextField(
            value = cervicalMucus,
            onValueChange = { cervicalMucus = it },
            label = { Text(stringResource(R.string.cervical_mucus)) },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Body temperature
        OutlinedTextField(
            value = bodyTemperature,
            onValueChange = { bodyTemperature = it },
            label = { Text(stringResource(R.string.body_temperature)) },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Notes
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(stringResource(R.string.notes)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        // Save and Delete buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Delete button
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.delete))
            }
            
            // Save button
            Button(
                onClick = {
                    viewModel.saveRecord(
                        date = selectedDate,
                        flowLevel = if (flowLevel > 0) flowLevel else null,
                        painLevel = if (painLevel > 0) painLevel else null,
                        symptoms = selectedSymptoms.toList(),
                        sexualActivity = hasSexualActivity,
                        ovulationTest = ovulationTest.takeIf { it.isNotEmpty() },
                        cervicalMucus = cervicalMucus.takeIf { it.isNotEmpty() },
                        bodyTemperature = bodyTemperature.toFloatOrNull(),
                        notes = notes.takeIf { it.isNotEmpty() }
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.weight(2f)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
