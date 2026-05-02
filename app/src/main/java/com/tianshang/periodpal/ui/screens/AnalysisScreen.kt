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
import com.tianshang.periodpal.data.model.CycleRegularity
import com.tianshang.periodpal.data.model.ConfidenceLevel
import com.tianshang.periodpal.ui.components.CycleLengthChart
import com.tianshang.periodpal.ui.components.PainTrendChart
import com.tianshang.periodpal.ui.components.SymptomFrequencyChart
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
                        value = "%.1f %s".format(stats.averageCycleLength, stringResource(R.string.unit_days))
                    )
                    StatisticRow(
                        label = stringResource(R.string.avg_period_length),
                        value = "%.1f %s".format(stats.averagePeriodLength, stringResource(R.string.unit_days))
                    )
                    StatisticRow(
                        label = stringResource(R.string.cycle_regularity),
                        value = mapRegularity(context, stats.cycleRegularity)
                    )
                    StatisticRow(
                        label = stringResource(R.string.total_cycles),
                        value = stats.totalCycles.toString()
                    )
                }
            }
            
            if (stats.cycleLengths.isNotEmpty()) {
                CycleLengthChart(
                    cycleLengths = stats.cycleLengths,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        
            if (stats.painTrend.isNotEmpty()) {
                PainTrendChart(
                    painTrend = stats.painTrend,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        
            if (stats.symptomFrequency.isNotEmpty()) {
                SymptomFrequencyChart(
                    symptomFrequency = stats.symptomFrequency,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
        
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
                                text = stringResource(R.string.predicted_period_label, prediction.periodStartDate, prediction.periodEndDate),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.ovulation_date_label, prediction.ovulationDate),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = stringResource(R.string.confidence_label, mapConfidence(context, prediction.confidence), prediction.basedOnCycles),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
        
        if (statistics == null || (statistics?.totalCycles ?: 0) < 2) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.analysis_no_data_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.analysis_no_data_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun mapRegularity(context: android.content.Context, regularity: String): String {
    return when (CycleRegularity.fromKey(regularity)) {
        CycleRegularity.REGULAR -> context.getString(R.string.regularity_regular)
        CycleRegularity.SOMEWHAT_REGULAR -> context.getString(R.string.regularity_somewhat_regular)
        CycleRegularity.IRREGULAR -> context.getString(R.string.regularity_irregular)
        CycleRegularity.INSUFFICIENT_DATA -> context.getString(R.string.regularity_insufficient_data)
    }
}

private fun mapConfidence(context: android.content.Context, confidence: String): String {
    return when (ConfidenceLevel.fromKey(confidence)) {
        ConfidenceLevel.HIGH -> context.getString(R.string.confidence_high)
        ConfidenceLevel.MEDIUM -> context.getString(R.string.confidence_medium)
        ConfidenceLevel.LOW -> context.getString(R.string.confidence_low)
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
