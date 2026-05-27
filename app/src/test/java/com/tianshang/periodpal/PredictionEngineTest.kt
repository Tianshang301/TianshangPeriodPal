package com.tianshang.periodpal

import org.junit.Test
import org.junit.Assert.*
import com.tianshang.periodpal.data.model.ConfidenceLevel
import com.tianshang.periodpal.data.model.CycleRegularity
import com.tianshang.periodpal.data.model.DailySymptom
import com.tianshang.periodpal.data.model.PeriodRecord
import com.tianshang.periodpal.utils.PredictionEngine
import java.time.LocalDate

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
}
