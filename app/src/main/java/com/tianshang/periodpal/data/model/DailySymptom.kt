package com.tianshang.periodpal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_symptoms")
data class DailySymptom(
    @PrimaryKey
    val date: LocalDate,
    val symptoms: String = "[]",
    val sexualActivity: Boolean? = null,
    val ovulationTestResult: String? = null,
    val cervicalMucus: String? = null,
    val bodyTemperature: Float? = null,
    val notes: String? = null
)
