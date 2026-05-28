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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class PredictionEngine {
    
    companion object {
        const val DEFAULT_CYCLE_LENGTH = 28
        const val DEFAULT_PERIOD_LENGTH = 5
        const val DEFAULT_LUTEAL_PHASE = 14
        const val MIN_CYCLES_FOR_PREDICTION = 3
        const val DECAY_FACTOR = 0.8
        const val MIN_LUTEAL_PHASE = 10
        const val MAX_LUTEAL_PHASE = 16
        const val MIN_CYCLE_LENGTH = 21
        const val MAX_CYCLE_LENGTH = 45
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
        
        // Optimization 1: Learn luteal phase from data
        val learnedLutealPhase = learnLutealPhase(sortedRecords, symptoms, lutealPhaseLength)
        
        val lastRecord = sortedRecords.last()
        val predictions = mutableListOf<CyclePrediction>()
        
        var nextStartDate = lastRecord.startDate.plusDays(averageCycleLength.toLong())
        
        // Optimization 2: Expanded ovulation adjustment range
        val ovulationAdjustment = findOvulationAdjustment(symptoms, lastRecord, averageCycleLength)
        if (ovulationAdjustment != null) {
            nextStartDate = ovulationAdjustment.plusDays(learnedLutealPhase.toLong())
        }
        
        val confidence = calculateConfidence(cycleLengths)
        
        for (i in 0 until 6) {
            val periodStart = nextStartDate.plusDays((i * averageCycleLength).toLong())
            val periodEnd = periodStart.plusDays(averagePeriodLength.toLong() - 1)
            val ovulationDate = periodStart.minusDays(learnedLutealPhase.toLong())
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
    
    // Optimization 1: Learn luteal phase from historical data
    private fun learnLutealPhase(
        records: List<PeriodRecord>,
        symptoms: List<DailySymptom>,
        defaultLutealPhase: Int
    ): Int {
        val lutealPhases = mutableListOf<Int>()
        
        for (i in 1 until records.size) {
            val prevRecord = records[i - 1]
            val currRecord = records[i]
            
            // Find ovulation confirmation in the previous cycle (after period ends)
            val searchStart = prevRecord.endDate?.plusDays(1)
                ?: prevRecord.startDate.plusDays(DEFAULT_PERIOD_LENGTH.toLong())
            val ovulationDate = findOvulationDateInRange(
                symptoms,
                searchStart,
                currRecord.startDate
            )
            
            if (ovulationDate != null) {
                val lutealDays = ChronoUnit.DAYS.between(ovulationDate, currRecord.startDate).toInt()
                if (lutealDays in MIN_LUTEAL_PHASE..MAX_LUTEAL_PHASE) {
                    lutealPhases.add(lutealDays)
                }
            }
        }
        
        return if (lutealPhases.isNotEmpty()) {
            // Use median for robustness
            val sorted = lutealPhases.sorted()
            sorted[sorted.size / 2]
        } else {
            defaultLutealPhase
        }
    }
    
    // Find ovulation date within a date range
    private fun findOvulationDateInRange(
        symptoms: List<DailySymptom>,
        startDate: LocalDate,
        endDate: LocalDate
    ): LocalDate? {
        val relevantSymptoms = symptoms.filter {
            !it.date.isBefore(startDate) && !it.date.isAfter(endDate)
        }
        
        // Priority 1: Ovulation test positive
        for (symptom in relevantSymptoms) {
            val testResult = OvulationTestResult.fromValue(symptom.ovulationTestResult)
            if (testResult == OvulationTestResult.POSITIVE) {
                return symptom.date
            }
        }
        
        // Priority 2: Egg white cervical mucus
        for (symptom in relevantSymptoms) {
            val mucusType = CervicalMucusType.fromValue(symptom.cervicalMucus)
            if (mucusType == CervicalMucusType.EGG_WHITE) {
                return symptom.date
            }
        }
        
        // Priority 3: Temperature rise (3 consecutive days > first + 0.3°C)
        val temps = relevantSymptoms.mapNotNull { symptom ->
            symptom.bodyTemperature?.let { symptom.date to it }
        }
        if (temps.size >= 3) {
            for (i in 0 until temps.size - 2) {
                if (temps[i + 1].second > temps[i].second + 0.29f &&
                    temps[i + 2].second > temps[i].second + 0.29f) {
                    return temps[i + 1].first
                }
            }
        }
        
        return null
    }
    
    // Optimization 5: Dynamic cycle length range based on IQR
    private fun calculateCycleLengths(records: List<PeriodRecord>): List<Int> {
        val rawLengths = mutableListOf<Int>()
        for (i in 1 until records.size) {
            val days = ChronoUnit.DAYS.between(records[i - 1].startDate, records[i].startDate).toInt()
            rawLengths.add(days)
        }
        
        if (rawLengths.isEmpty()) return emptyList()
        
        // Calculate dynamic bounds using IQR
        val sorted = rawLengths.sorted()
        val q1 = sorted[sorted.size / 4]
        val q3 = sorted[sorted.size * 3 / 4]
        val iqr = q3 - q1
        val lowerBound = max(MIN_CYCLE_LENGTH.toDouble(), q1 - 1.5 * iqr)
        val upperBound = min(MAX_CYCLE_LENGTH.toDouble(), q3 + 1.5 * iqr)
        
        return rawLengths.filter { it.toDouble() in lowerBound..upperBound }
    }
    
    // Optimization 4: Exponential decay weighting
    private fun calculateWeightedAverage(lengths: List<Int>): Double {
        if (lengths.isEmpty()) return DEFAULT_CYCLE_LENGTH.toDouble()
        
        // Exponential decay: recent data has higher weight
        var sum = 0.0
        var weightSum = 0.0
        lengths.forEachIndexed { index, length ->
            val weight = DECAY_FACTOR.pow((lengths.size - 1 - index).toDouble())
            sum += length * weight
            weightSum += weight
        }
        
        return sum / weightSum
    }
    
    // Optimization 3: IQR filtering for period lengths
    private fun calculateAveragePeriodLength(records: List<PeriodRecord>): Double {
        val lengths = records.mapNotNull { record ->
            record.endDate?.let {
                ChronoUnit.DAYS.between(record.startDate, it).toInt() + 1
            }
        }
        if (lengths.isEmpty()) return DEFAULT_PERIOD_LENGTH.toDouble()
        
        // IQR filtering
        val sorted = lengths.sorted()
        val q1 = sorted[sorted.size / 4]
        val q3 = sorted[sorted.size * 3 / 4]
        val iqr = q3 - q1
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr
        
        val filtered = lengths.filter { it.toDouble() in lowerBound..upperBound }
        return if (filtered.isNotEmpty()) filtered.average() else lengths.average()
    }
    
    // Optimization 2: Expanded ovulation adjustment range
    private fun findOvulationAdjustment(
        symptoms: List<DailySymptom>,
        lastRecord: PeriodRecord,
        averageCycleLength: Double
    ): LocalDate? {
        // Check from lastRecord.startDate to predicted next start date
        val checkEndDate = lastRecord.startDate.plusDays(averageCycleLength.toLong())
        
        val relevantSymptoms = symptoms.filter {
            it.date.isAfter(lastRecord.startDate) && !it.date.isAfter(checkEndDate)
        }
        
        relevantSymptoms.forEach { symptom ->
            val testResult = OvulationTestResult.fromValue(symptom.ovulationTestResult)
            val mucusType = CervicalMucusType.fromValue(symptom.cervicalMucus)
            if (testResult == OvulationTestResult.POSITIVE || mucusType == CervicalMucusType.EGG_WHITE) {
                return symptom.date
            }
        }
        
        val temps = relevantSymptoms.mapNotNull { symptom ->
            symptom.bodyTemperature?.let { symptom.date to it }
        }
        if (temps.size >= 3) {
            for (i in 0 until temps.size - 2) {
                if (temps[i + 1].second > temps[i].second + 0.29f &&
                    temps[i + 2].second > temps[i].second + 0.29f) {
                    return temps[i + 1].first
                }
            }
        }
        
        return null
    }
    
    private fun calculateConfidence(cycleLengths: List<Int>): ConfidenceLevel {
        if (cycleLengths.size < 3) return ConfidenceLevel.LOW
        
        val mean = cycleLengths.average()
        val variance = cycleLengths.map { (it - mean) * (it - mean) }.average()
        val stdDev = sqrt(variance)
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
            val stdDev = sqrt(variance)
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
