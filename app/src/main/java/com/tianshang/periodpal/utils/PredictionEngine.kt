package com.tianshang.periodpal.utils

import com.tianshang.periodpal.data.model.CervicalMucusType
import com.tianshang.periodpal.data.model.ConfidenceLevel
import com.tianshang.periodpal.data.model.CyclePrediction
import com.tianshang.periodpal.data.model.CycleRegularity
import com.tianshang.periodpal.data.model.CycleStatistics
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.OvulationTestResult
import com.tianshang.periodpal.data.model.PeriodRecord
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class PredictionEngine {
    
    companion object {
        const val DEFAULT_CYCLE_LENGTH = 28
        const val DEFAULT_PERIOD_LENGTH = 5
        const val DEFAULT_LUTEAL_PHASE = 14
        const val MIN_CYCLES_FOR_PREDICTION = 3
    }
    
    fun predictNextCycles(
        records: List<PeriodRecord>,
        symptoms: List<DailySymptom>,
        lutealPhaseLength: Int = DEFAULT_LUTEAL_PHASE
    ): List<CyclePrediction> {
        if (records.size < MIN_CYCLES_FOR_PREDICTION) {
            return emptyList()
        }
        
        val sortedRecords = records.filter { !it.isDeleted }.sortedBy { it.startDate }
        val cycleLengths = calculateCycleLengths(sortedRecords)
        val averageCycleLength = calculateWeightedAverage(cycleLengths)
        val averagePeriodLength = calculateAveragePeriodLength(sortedRecords)
        
        val lastRecord = sortedRecords.last()
        val predictions = mutableListOf<CyclePrediction>()
        
        var nextStartDate = lastRecord.startDate.plusDays(averageCycleLength.toLong())
        
        val ovulationAdjustment = findOvulationAdjustment(symptoms, lastRecord)
        if (ovulationAdjustment != null) {
            nextStartDate = ovulationAdjustment.plusDays(lutealPhaseLength.toLong())
        }
        
        val confidence = calculateConfidence(cycleLengths)
        
        for (i in 0 until 6) {
            val periodStart = nextStartDate.plusDays((i * averageCycleLength).toLong())
            val periodEnd = periodStart.plusDays(averagePeriodLength.toLong() - 1)
            val ovulationDate = periodStart.minusDays(lutealPhaseLength.toLong())
            val fertileStart = ovulationDate.minusDays(5)
            val fertileEnd = ovulationDate.plusDays(1)
            
            predictions.add(
                CyclePrediction(
                    periodStartDate = periodStart,
                    periodEndDate = periodEnd,
                    ovulationDate = ovulationDate,
                    fertileWindowStart = fertileStart,
                    fertileWindowEnd = fertileEnd,
                    confidence = confidence.key,
                    basedOnCycles = sortedRecords.size
                )
            )
        }
        
        return predictions
    }
    
    private fun calculateCycleLengths(records: List<PeriodRecord>): List<Int> {
        val lengths = mutableListOf<Int>()
        for (i in 1 until records.size) {
            val days = ChronoUnit.DAYS.between(records[i - 1].startDate, records[i].startDate).toInt()
            if (days in 21..35) {
                lengths.add(days)
            }
        }
        return lengths
    }
    
    private fun calculateWeightedAverage(lengths: List<Int>): Double {
        if (lengths.isEmpty()) return DEFAULT_CYCLE_LENGTH.toDouble()
        
        val sorted = lengths.sorted()
        val q1 = sorted[sorted.size / 4]
        val q3 = sorted[sorted.size * 3 / 4]
        val iqr = q3 - q1
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr
        
        val filtered = lengths.filter { it.toDouble() in lowerBound..upperBound }
        if (filtered.isEmpty()) return lengths.average()
        
        var sum = 0.0
        var weightSum = 0.0
        filtered.forEachIndexed { index, length ->
            val weight = index + 1.0
            sum += length * weight
            weightSum += weight
        }
        
        return sum / weightSum
    }
    
    private fun calculateAveragePeriodLength(records: List<PeriodRecord>): Double {
        val lengths = records.mapNotNull { record ->
            record.endDate?.let {
                ChronoUnit.DAYS.between(record.startDate, it).toInt() + 1
            }
        }
        return if (lengths.isNotEmpty()) lengths.average() else DEFAULT_PERIOD_LENGTH.toDouble()
    }
    
    private fun findOvulationAdjustment(symptoms: List<DailySymptom>, lastRecord: PeriodRecord): LocalDate? {
        val relevantSymptoms = symptoms.filter { 
            it.date.isAfter(lastRecord.startDate) && it.date.isBefore(lastRecord.startDate.plusDays(20))
        }
        
        relevantSymptoms.forEach { symptom ->
            val testResult = OvulationTestResult.fromValue(symptom.ovulationTestResult)
            val mucusType = CervicalMucusType.fromValue(symptom.cervicalMucus)
            if (testResult == OvulationTestResult.POSITIVE || mucusType == CervicalMucusType.EGG_WHITE) {
                return symptom.date
            }
        }
        
        val temps = relevantSymptoms.mapNotNull { it.bodyTemperature }
        if (temps.size >= 3) {
            for (i in 0 until temps.size - 2) {
                if (temps[i + 1] > temps[i] + 0.3 && temps[i + 2] > temps[i] + 0.3) {
                    return relevantSymptoms[i + 1].date
                }
            }
        }
        
        return null
    }
    
    private fun calculateConfidence(cycleLengths: List<Int>): ConfidenceLevel {
        if (cycleLengths.size < 3) return ConfidenceLevel.LOW
        
        val mean = cycleLengths.average()
        val variance = cycleLengths.map { (it - mean) * (it - mean) }.average()
        val stdDev = kotlin.math.sqrt(variance)
        val cv = stdDev / mean
        
        return when {
            cv < 0.05 -> ConfidenceLevel.HIGH
            cv < 0.1 -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }
    }
    
    fun calculateStatistics(records: List<PeriodRecord>, symptoms: List<DailySymptom>): CycleStatistics {
        val sortedRecords = records.filter { !it.isDeleted }.sortedBy { it.startDate }
        val cycleLengths = calculateCycleLengths(sortedRecords)
        val periodLengths = sortedRecords.mapNotNull { record ->
            record.endDate?.let {
                ChronoUnit.DAYS.between(record.startDate, it).toInt() + 1
            }
        }
        
        val avgCycleLength = if (cycleLengths.isNotEmpty()) cycleLengths.average() else 0.0
        val avgPeriodLength = if (periodLengths.isNotEmpty()) periodLengths.average() else 0.0
        
        val regularity = if (cycleLengths.size >= 3) {
            val mean = cycleLengths.average()
            val variance = cycleLengths.map { (it - mean) * (it - mean) }.average()
            val stdDev = kotlin.math.sqrt(variance)
            val cv = stdDev / mean
            
            when {
                cv < 0.05 -> CycleRegularity.REGULAR
                cv < 0.1 -> CycleRegularity.SOMEWHAT_REGULAR
                else -> CycleRegularity.IRREGULAR
            }
        } else {
            CycleRegularity.INSUFFICIENT_DATA
        }
        
        val painTrend = sortedRecords.mapIndexed { index, record ->
            index to (record.painLevel?.toDouble() ?: 0.0)
        }
        
        val cycleLengthsWithIndex = cycleLengths.mapIndexed { index, length ->
            index to length
        }
        
        val symptomFrequency = mutableMapOf<String, Int>()
        symptoms.forEach { symptom ->
            val symptomList = parseSymptoms(symptom.symptoms)
            symptomList.forEach { s ->
                symptomFrequency[s] = symptomFrequency.getOrDefault(s, 0) + 1
            }
        }
        
        return CycleStatistics(
            averageCycleLength = avgCycleLength,
            averagePeriodLength = avgPeriodLength,
            cycleRegularity = regularity.key,
            totalCycles = sortedRecords.size,
            painTrend = painTrend,
            symptomFrequency = symptomFrequency,
            cycleLengths = cycleLengthsWithIndex
        )
    }
    
    private fun parseSymptoms(symptomsJson: String): List<String> {
        return try {
            symptomsJson.removePrefix("[").removeSuffix("]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotEmpty() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
