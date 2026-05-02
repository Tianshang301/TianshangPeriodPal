package com.tianshang.periodpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tianshang.periodpal.R
import com.tianshang.periodpal.data.model.CyclePrediction
import com.tianshang.periodpal.data.model.PeriodRecord
import com.tianshang.periodpal.ui.navigation.Screen
import com.tianshang.periodpal.ui.theme.*
import com.tianshang.periodpal.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModel.Factory(context)
    )
    
    val currentMonth by viewModel.currentMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val records by viewModel.records.collectAsState()
    val predictions by viewModel.predictions.collectAsState()
    val currentCycleDay by viewModel.currentCycleDay.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PinkPrimary.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.current_cycle_day, currentCycleDay),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(R.string.tap_date_to_record),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(Icons.Default.KeyboardArrowLeft, stringResource(R.string.calendar_previous_month))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern(stringResource(R.string.calendar_date_format)).withLocale(java.util.Locale.getDefault())),
                    style = MaterialTheme.typography.titleLarge
                )
                if (currentMonth != YearMonth.now()) {
                    TextButton(onClick = {
                        viewModel.goToToday()
                    }) {
                        Text(
                            text = stringResource(R.string.calendar_today),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            IconButton(onClick = { viewModel.nextMonth() }) {
                Icon(Icons.Default.KeyboardArrowRight, stringResource(R.string.calendar_next_month))
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf(
                stringResource(R.string.week_sun),
                stringResource(R.string.week_mon),
                stringResource(R.string.week_tue),
                stringResource(R.string.week_wed),
                stringResource(R.string.week_thu),
                stringResource(R.string.week_fri),
                stringResource(R.string.week_sat)
            ).forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        CalendarGrid(
            modifier = Modifier.weight(1f),
            yearMonth = currentMonth,
            records = records,
            predictions = predictions,
            selectedDate = selectedDate,
            onDateSelected = { date ->
                viewModel.selectDate(date)
                navController.navigate(Screen.RecordWithDate.createRoute(date.toString()))
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = PeriodRed, text = stringResource(R.string.period))
                LegendItem(color = PeriodRedLight, text = stringResource(R.string.predicted_period))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = OvulationBlue, text = stringResource(R.string.ovulation))
                LegendItem(color = OvulationBlueLight, text = stringResource(R.string.fertile_window))
            }
        }
        
        FloatingActionButton(
            onClick = { 
                selectedDate?.let { date ->
                    navController.navigate(Screen.RecordWithDate.createRoute(date.toString()))
                } ?: navController.navigate(Screen.Record.route)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, stringResource(R.string.calendar_add_record))
        }
    }
}

@Composable
fun CalendarGrid(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    records: List<PeriodRecord>,
    predictions: List<CyclePrediction>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startOffset = firstDayOfMonth.dayOfWeek.value % 7
    
    val weeks = mutableListOf<List<LocalDate?>>()
    var currentWeek = MutableList<LocalDate?>(startOffset) { null }
    
    for (day in 1..daysInMonth) {
        currentWeek.add(yearMonth.atDay(day))
        if (currentWeek.size == 7) {
            weeks.add(currentWeek)
            currentWeek = mutableListOf()
        }
    }
    if (currentWeek.isNotEmpty()) {
        while (currentWeek.size < 7) {
            currentWeek.add(null)
        }
        weeks.add(currentWeek)
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    if (date != null) {
                        val isPeriod = records.any { record ->
                            !record.isDeleted &&
                            !date.isBefore(record.startDate) &&
                            (record.endDate == null || !date.isAfter(record.endDate))
                        }
                        val isPredictedPeriod = predictions.any { prediction ->
                            !date.isBefore(prediction.periodStartDate) &&
                            !date.isAfter(prediction.periodEndDate)
                        }
                        val isOvulation = predictions.any { prediction ->
                            date == prediction.ovulationDate
                        }
                        val isFertile = predictions.any { prediction ->
                            !date.isBefore(prediction.fertileWindowStart) &&
                            !date.isAfter(prediction.fertileWindowEnd)
                        }
                        val backgroundColor = when {
                            isPeriod -> PeriodRed
                            isOvulation -> OvulationBlue
                            isFertile -> OvulationBlueLight
                            isPredictedPeriod -> PeriodRedLight
                            else -> Color.Transparent
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .background(
                                    color = backgroundColor,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = if (backgroundColor != Color.Transparent) Color.White
                                       else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}
