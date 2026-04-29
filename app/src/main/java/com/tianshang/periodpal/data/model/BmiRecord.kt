package com.tianshang.periodpal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "bmi_records")
data class BmiRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val height: Float,
    val weight: Float,
    val bmi: Float,
    val notes: String? = null
)
