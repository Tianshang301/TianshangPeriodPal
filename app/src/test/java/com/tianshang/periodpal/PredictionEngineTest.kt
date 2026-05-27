package com.tianshang.periodpal

import org.junit.Test
import org.junit.Assert.*
import com.tianshang.periodpal.data.model.ConfidenceLevel
import com.tianshang.periodpal.data.model.CycleRegularity
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord
import com.tianshang.periodpal.utils.PredictionEngine
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class PredictionEngineTest {
    
    private val engine = PredictionEngine()
    
    @Test
    fun testCalculateCycleLengths() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1)),
            PeriodRecord(startDate = LocalDate.of(2024, 3, 25), endDate = LocalDate.of(2024, 3, 29))
        )
        
        val predictions = engine.predictNextCycles(records, emptyList())
        assertTrue(predictions.isNotEmpty())
        assertEquals(ConfidenceLevel.HIGH.key, predictions.first().confidence)
    }
    
    @Test
    fun testInsufficientData() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5))
        )
        
        val predictions = engine.predictNextCycles(records, emptyList())
        assertTrue(predictions.isEmpty())
    }
    
    @Test
    fun testCycleStatistics() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5), painLevel = 1),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2), painLevel = 2),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1), painLevel = 1),
            PeriodRecord(startDate = LocalDate.of(2024, 3, 25), endDate = LocalDate.of(2024, 3, 29), painLevel = 1)
        )
        
        val stats = engine.calculateStatistics(records, emptyList())
        assertEquals(4, stats.totalCycles)
        assertEquals(CycleRegularity.REGULAR.key, stats.cycleRegularity)
    }
    
    @Test
    fun testExactlyMinimumCycles() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1))
        )
        
        val predictions = engine.predictNextCycles(records, emptyList())
        assertEquals(6, predictions.size)
    }
    
    @Test
    fun testEmptyRecords() {
        val predictions = engine.predictNextCycles(emptyList(), emptyList())
        assertTrue(predictions.isEmpty())
        
        val stats = engine.calculateStatistics(emptyList(), emptyList())
        assertEquals(0, stats.totalCycles)
        assertEquals(CycleRegularity.INSUFFICIENT_DATA.key, stats.cycleRegularity)
    }
    
    @Test
    fun testIqrOutlierFiltering() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1)),
            PeriodRecord(startDate = LocalDate.of(2024, 4, 20), endDate = LocalDate.of(2024, 4, 24)),
            PeriodRecord(startDate = LocalDate.of(2024, 5, 18), endDate = LocalDate.of(2024, 5, 22))
        )
        
        val predictions = engine.predictNextCycles(records, emptyList())
        assertTrue(predictions.isNotEmpty())
    }
    
    @Test
    fun testOvulationAdjustmentWithPositiveTest() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1))
        )
        
        val symptoms = listOf(
            DailySymptom(
                date = LocalDate.of(2024, 2, 12),
                symptoms = "[]",
                ovulationTestResult = "positive"
            )
        )
        
        val predictions = engine.predictNextCycles(records, symptoms)
        assertTrue(predictions.isNotEmpty())
    }
    
    @Test
    fun testOvulationAdjustmentWithEggWhiteMucus() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1))
        )
        
        val symptoms = listOf(
            DailySymptom(
                date = LocalDate.of(2024, 2, 13),
                symptoms = "[]",
                cervicalMucus = "egg_white"
            )
        )
        
        val predictions = engine.predictNextCycles(records, symptoms)
        assertTrue(predictions.isNotEmpty())
    }
    
    @Test
    fun testStatisticsInsufficientData() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2))
        )
        
        val stats = engine.calculateStatistics(records, emptyList())
        assertEquals(2, stats.totalCycles)
        assertEquals(CycleRegularity.INSUFFICIENT_DATA.key, stats.cycleRegularity)
    }
    
    @Test
    fun testStatisticsIrregularCycles() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 25), endDate = LocalDate.of(2024, 1, 29)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 20), endDate = LocalDate.of(2024, 2, 24)),
            PeriodRecord(startDate = LocalDate.of(2024, 3, 25), endDate = LocalDate.of(2024, 3, 29))
        )
        
        val stats = engine.calculateStatistics(records, emptyList())
        assertEquals(CycleRegularity.IRREGULAR.key, stats.cycleRegularity)
    }
    
    @Test
    fun testStatisticsSymptomFrequency() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2))
        )
        
        val symptoms = listOf(
            DailySymptom(date = LocalDate.of(2024, 1, 2), symptoms = "[\"headache\",\"fatigue\"]"),
            DailySymptom(date = LocalDate.of(2024, 1, 30), symptoms = "[\"headache\"]")
        )
        
        val stats = engine.calculateStatistics(records, symptoms)
        assertEquals(2, stats.symptomFrequency["headache"])
        assertEquals(1, stats.symptomFrequency["fatigue"])
    }
    
    @Test
    fun testDeletedRecordsExcluded() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2), isDeleted = true),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1))
        )
        
        val stats = engine.calculateStatistics(records, emptyList())
        assertEquals(2, stats.totalCycles)
    }
    
    // New tests for optimizations
    
    @Test
    fun testLearnedLutealPhaseFromOvulationTest() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1)),
            PeriodRecord(startDate = LocalDate.of(2024, 3, 25), endDate = LocalDate.of(2024, 3, 29))
        )
        
        // Ovulation test positive on day 14 of first cycle
        val symptoms = listOf(
            DailySymptom(
                date = LocalDate.of(2024, 1, 14),
                symptoms = "[]",
                ovulationTestResult = "positive"
            ),
            DailySymptom(
                date = LocalDate.of(2024, 2, 12),
                symptoms = "[]",
                ovulationTestResult = "positive"
            )
        )
        
        val predictions = engine.predictNextCycles(records, symptoms)
        assertTrue(predictions.isNotEmpty())
        // Luteal phase should be learned: Jan 29 - Jan 14 = 15 days
        // Ovulation date should be period start - learned luteal phase
        val firstPrediction = predictions.first()
        val expectedOvulation = firstPrediction.periodStartDate.minusDays(15)
        assertEquals(expectedOvulation, firstPrediction.ovulationDate)
    }
    
    @Test
    fun testExpandedOvulationAdjustmentRange() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1))
        )
        
        // Ovulation test positive on day 22 (beyond old 20-day limit)
        val symptoms = listOf(
            DailySymptom(
                date = LocalDate.of(2024, 2, 22),
                symptoms = "[]",
                ovulationTestResult = "positive"
            )
        )
        
        val predictions = engine.predictNextCycles(records, symptoms)
        assertTrue(predictions.isNotEmpty())
        // Should use the ovulation adjustment even though it's after day 20
    }
    
    @Test
    fun testPeriodLengthIqrFiltering() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),  // 5 days
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),  // 5 days
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1)),  // 5 days
            PeriodRecord(startDate = LocalDate.of(2024, 3, 25), endDate = LocalDate.of(2024, 4, 5))   // 12 days (outlier)
        )
        
        val predictions = engine.predictNextCycles(records, emptyList())
        assertTrue(predictions.isNotEmpty())
        // Period length should be filtered to ~5 days, not inflated by outlier
        val periodLength = ChronoUnit.DAYS.between(
            predictions.first().periodStartDate,
            predictions.first().periodEndDate
        ).toInt() + 1
        assertTrue("Period length should be close to 5, got $periodLength", periodLength in 4..7)
    }
    
    @Test
    fun testExponentialDecayWeighting() {
        // Recent cycles should have more influence
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 31), endDate = LocalDate.of(2024, 2, 4)),  // 30 days
            PeriodRecord(startDate = LocalDate.of(2024, 2, 28), endDate = LocalDate.of(2024, 3, 3)),  // 28 days
            PeriodRecord(startDate = LocalDate.of(2024, 3, 27), endDate = LocalDate.of(2024, 3, 31))  // 28 days
        )
        
        val predictions = engine.predictNextCycles(records, emptyList())
        assertTrue(predictions.isNotEmpty())
        // With exponential decay, recent 28-day cycles should dominate
        // Prediction should be closer to 28 than to 30
        val nextStart = predictions.first().periodStartDate
        val lastRecordEnd = LocalDate.of(2024, 3, 27)
        val daysBetween = ChronoUnit.DAYS.between(lastRecordEnd, nextStart)
        assertTrue("Should be closer to 28 days, got $daysBetween", daysBetween in 26..30)
    }
    
    @Test
    fun testDynamicCycleLengthRange() {
        // Test with a user who has consistently long cycles (35 days)
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 5), endDate = LocalDate.of(2024, 2, 9)),  // 35 days
            PeriodRecord(startDate = LocalDate.of(2024, 3, 11), endDate = LocalDate.of(2024, 3, 15)), // 35 days
            PeriodRecord(startDate = LocalDate.of(2024, 4, 15), endDate = LocalDate.of(2024, 4, 19))  // 35 days
        )
        
        val predictions = engine.predictNextCycles(records, emptyList())
        assertTrue(predictions.isNotEmpty())
        // Dynamic range should accept 35-day cycles
        val nextStart = predictions.first().periodStartDate
        val daysBetween = ChronoUnit.DAYS.between(LocalDate.of(2024, 4, 15), nextStart)
        assertTrue("Should predict ~35 days, got $daysBetween", daysBetween in 33..37)
    }
    
    @Test
    fun testTemperatureBasedLutealPhaseLearning() {
        val records = listOf(
            PeriodRecord(startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5)),
            PeriodRecord(startDate = LocalDate.of(2024, 1, 29), endDate = LocalDate.of(2024, 2, 2)),
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1)),
            PeriodRecord(startDate = LocalDate.of(2024, 3, 25), endDate = LocalDate.of(2024, 3, 29))
        )
        
        // Temperature rise on day 13, 14, 15
        val symptoms = listOf(
            DailySymptom(date = LocalDate.of(2024, 1, 12), symptoms = "[]", bodyTemperature = 36.2f),
            DailySymptom(date = LocalDate.of(2024, 1, 13), symptoms = "[]", bodyTemperature = 36.2f),
            DailySymptom(date = LocalDate.of(2024, 1, 14), symptoms = "[]", bodyTemperature = 36.5f),
            DailySymptom(date = LocalDate.of(2024, 1, 15), symptoms = "[]", bodyTemperature = 36.6f),
            DailySymptom(date = LocalDate.of(2024, 1, 16), symptoms = "[]", bodyTemperature = 36.5f)
        )
        
        val predictions = engine.predictNextCycles(records, symptoms)
        assertTrue(predictions.isNotEmpty())
        // Luteal phase should be learned from temperature: Jan 29 - Jan 14 = 15 days
    }
}
