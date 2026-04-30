package com.tianshang.periodpal.data.model

data class CycleStatistics(
    val averageCycleLength: Double,
    val averagePeriodLength: Double,
    val cycleRegularity: String,
    val totalCycles: Int,
    val painTrend: List<Pair<Int, Double>>,
    val symptomFrequency: Map<String, Int>,
    val cycleLengths: List<Pair<Int, Int>> = emptyList() // Pair<cycleIndex, length>
)
