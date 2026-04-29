package com.tianshang.periodpal.data.model

data class CyclePrediction(
    val periodStartDate: java.time.LocalDate,
    val periodEndDate: java.time.LocalDate,
    val ovulationDate: java.time.LocalDate,
    val fertileWindowStart: java.time.LocalDate,
    val fertileWindowEnd: java.time.LocalDate,
    val confidence: String,
    val basedOnCycles: Int
)
