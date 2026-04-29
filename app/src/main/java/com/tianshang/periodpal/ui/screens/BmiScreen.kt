package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.model.BmiRecord
import com.tianshang.periodpal.viewmodel.BmiViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: BmiViewModel = viewModel(
        factory = BmiViewModel.Factory(context)
    )
    
    val allRecords by viewModel.allRecords.collectAsState()
    val latestRecord by viewModel.latestRecord.collectAsState()
    
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.bmi_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Latest BMI Card
            latestRecord?.let { record ->
                BmiCard(record = record, viewModel = viewModel)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Input Section
            Text(
                text = stringResource(R.string.add_bmi_record),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text(stringResource(R.string.height_cm)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text("cm") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text(stringResource(R.string.weight_kg)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text("kg") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    val h = height.toFloatOrNull()
                    val w = weight.toFloatOrNull()
                    if (h != null && w != null && h > 0 && w > 0) {
                        viewModel.addRecord(h, w, notes.takeIf { it.isNotEmpty() })
                        height = ""
                        weight = ""
                        notes = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = height.isNotEmpty() && weight.isNotEmpty()
            ) {
                Text(stringResource(R.string.save))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // History Section
            if (allRecords.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.bmi_history),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                allRecords.forEach { record ->
                    BmiHistoryItem(
                        record = record,
                        viewModel = viewModel,
                        onDelete = { viewModel.deleteRecord(record) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun BmiCard(record: BmiRecord, viewModel: BmiViewModel) {
    val category = viewModel.getBmiCategory(record.bmi)
    val categoryColor = when (category) {
        "偏瘦" -> MaterialTheme.colorScheme.primary
        "正常" -> MaterialTheme.colorScheme.tertiary
        "偏胖" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = categoryColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.latest_bmi),
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "%.1f".format(record.bmi),
                style = MaterialTheme.typography.displayLarge,
                color = categoryColor
            )
            
            Text(
                text = category,
                style = MaterialTheme.typography.titleMedium,
                color = categoryColor
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1f".format(record.height),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.height_cm),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1f".format(record.weight),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.weight_kg),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            record.notes?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun BmiHistoryItem(
    record: BmiRecord,
    viewModel: BmiViewModel,
    onDelete: () -> Unit
) {
    val category = viewModel.getBmiCategory(record.bmi)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    Card(
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
                    text = record.date.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "BMI: %.1f (${category})".format(record.bmi),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${record.height}cm / ${record.weight}kg",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
