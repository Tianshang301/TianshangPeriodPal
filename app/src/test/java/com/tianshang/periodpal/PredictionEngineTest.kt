package com.tianshang.periodpal

import org.junit.Test
import org.junit.Assert.*
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
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1))
        )
        
        val predictions = engine.predictNextCycles(records, emptyList())
        assertTrue(predictions.isNotEmpty())
        assertEquals("高", predictions.first().confidence)
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
            PeriodRecord(startDate = LocalDate.of(2024, 2, 26), endDate = LocalDate.of(2024, 3, 1), painLevel = 1)
        )
        
        val stats = engine.calculateStatistics(records, emptyList())
        assertEquals(3, stats.totalCycles)
        assertEquals("规律", stats.cycleRegularity)
    }
}
