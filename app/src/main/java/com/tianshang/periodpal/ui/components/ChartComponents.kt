package com.tianshang.periodpal.ui.components

import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.tianshang.periodpal.R
import com.tianshang.periodpal.ui.theme.*

@Composable
fun CycleLengthChart(
    cycleLengths: List<Pair<Int, Int>>, // Pair<cycleIndex, length>
    modifier: Modifier = Modifier
) {
    if (cycleLengths.isEmpty()) return
    val context = LocalContext.current
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.chart_title_cycle_length_trend),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            AndroidView(
                factory = { context ->
                    LineChart(context).apply {
                        description.isEnabled = false
                        setTouchEnabled(true)
                        setPinchZoom(true)
                        setDrawGridBackground(false)
                        
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.granularity = 1f
                        xAxis.setDrawGridLines(false)
                        
                        axisLeft.setDrawGridLines(true)
                        axisLeft.axisMinimum = 20f
                        axisLeft.axisMaximum = 40f
                        axisRight.isEnabled = false
                        
                        legend.isEnabled = false
                    }
                },
                update = { chart ->
                    val entries = cycleLengths.mapIndexed { index, (_, length) ->
                        Entry(index.toFloat(), length.toFloat())
                    }
                    
                    val dataSet = LineDataSet(entries, context.getString(R.string.chart_cycle_length_label)).apply {
                        color = PinkPrimary.toArgb()
                        setCircleColor(PinkPrimary.toArgb())
                        lineWidth = 2f
                        circleRadius = 4f
                        setDrawCircleHole(false)
                        valueTextSize = 10f
                        setDrawFilled(true)
                        fillColor = PinkPrimary.toArgb()
                        fillAlpha = 50
                    }
                    
                    chart.data = LineData(dataSet)
                    
                    val labels = cycleLengths.map { context.getString(R.string.chart_cycle_label, it.first + 1) }
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    
                    chart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

@Composable
fun PainTrendChart(
    painTrend: List<Pair<Int, Double>>, // Pair<cycleIndex, painLevel>
    modifier: Modifier = Modifier
) {
    if (painTrend.isEmpty()) return
    val context = LocalContext.current
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.chart_title_pain_trend),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        description.isEnabled = false
                        setTouchEnabled(true)
                        setPinchZoom(false)
                        setDrawGridBackground(false)
                        
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.granularity = 1f
                        xAxis.setDrawGridLines(false)
                        
                        axisLeft.axisMinimum = 0f
                        axisLeft.axisMaximum = 4f
                        axisRight.isEnabled = false
                        
                        legend.isEnabled = false
                    }
                },
                update = { chart ->
                    val entries = painTrend.mapIndexed { index, (_, pain) ->
                        BarEntry(index.toFloat(), pain.toFloat())
                    }
                    
                    val dataSet = BarDataSet(entries, context.getString(R.string.chart_pain_level_label)).apply {
                        colors = listOf(
                            PainNone.toArgb(),
                            PainMild.toArgb(),
                            PainModerate.toArgb(),
                            PainSevere.toArgb()
                        )
                        valueTextSize = 10f
                    }
                    
                    chart.data = BarData(dataSet).apply {
                        barWidth = 0.7f
                    }
                    
                    val labels = painTrend.map { context.getString(R.string.chart_cycle_label, it.first + 1) }
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    
                    chart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

@Composable
fun SymptomFrequencyChart(
    symptomFrequency: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    if (symptomFrequency.isEmpty()) return
    val ctx = LocalContext.current
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.chart_title_symptom_distribution),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            AndroidView(
                factory = { ctx ->
                    PieChart(ctx).apply {
                        description.isEnabled = false
                        setTouchEnabled(true)
                        setDrawEntryLabels(false)
                        centerText = ctx.getString(R.string.chart_title_symptom_distribution)
                        setCenterTextSize(14f)
                        setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                        legend.orientation = Legend.LegendOrientation.VERTICAL
                        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    }
                },
                update = { chart ->
                    val sortedSymptoms = symptomFrequency.entries
                        .sortedByDescending { it.value }
                        .take(6)
                    
                    val entries = sortedSymptoms.map { (symptom, count) ->
                        PieEntry(count.toFloat(), symptom)
                    }
                    
                    val dataSet = PieDataSet(entries, "").apply {
                        colors = ColorTemplate.MATERIAL_COLORS.toList()
                        valueTextSize = 12f
                        valueTextColor = Color.White.toArgb()
                    }
                    
                    chart.data = PieData(dataSet)
                    chart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}
