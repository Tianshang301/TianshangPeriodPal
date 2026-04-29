package com.tianshang.periodpal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "period_records")
data class PeriodRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val flowLevel: Int? = null,
    val painLevel: Int? = null,
    val notes: String? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)
