package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tianshang.periodpal.R
import com.tianshang.periodpal.viewmodel.AnalysisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AnalysisViewModel = viewModel(
        factory = AnalysisViewModel.Factory(context)
    )
    
    val statistics by viewModel.statistics.collectAsState()
    val predictions by viewModel.predictions.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Statistics cards
        statistics?.let { stats ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.cycle_statistics),
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    StatisticRow(
                        label = stringResource(R.string.avg_cycle_length),
                        value = "%.1f 天".format(stats.averageCycleLength)
                    )
                    StatisticRow(
                        label = stringResource(R.string.avg_period_length),
                        value = "%.1f 天".format(stats.averagePeriodLength)
                    )
                    StatisticRow(
                        label = stringResource(R.string.cycle_regularity),
                        value = stats.cycleRegularity
                    )
                    StatisticRow(
                        label = stringResource(R.string.total_cycles),
                        value = stats.totalCycles.toString()
                    )
                }
            }
        
            // Pain trend
            if (stats.painTrend.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.pain_trend),
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        stats.painTrend.forEach { (cycle, pain) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("第 ${cycle + 1} 周期")
                                Text("疼痛等级: %.1f".format(pain))
                            }
                        }
                    }
                }
            }
        
            // Symptom frequency
            if (stats.symptomFrequency.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.symptom_frequency),
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        stats.symptomFrequency.entries
                            .sortedByDescending { it.value }
                            .forEach { (symptom, count) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(symptom)
                                    Text("${count} 次")
                                }
                            }
                    }
                }
            }
        }
        
        // Predictions
        if (predictions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.predictions),
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    predictions.take(3).forEach { prediction ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                "预测经期: ${prediction.periodStartDate} - ${prediction.periodEndDate}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "排卵日: ${prediction.ovulationDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                "置信度: ${prediction.confidence} (基于 ${prediction.basedOnCycles} 个周期)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
