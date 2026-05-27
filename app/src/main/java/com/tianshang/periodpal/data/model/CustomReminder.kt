package com.tianshang.periodpal.data.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class CustomReminder(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String = "",
    val reminderDateTime: Long,
    val enabled: Boolean = true
) {
    fun getLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderDateTime), ZoneId.systemDefault())
    }
    
    companion object {
        fun fromLocalDateTime(title: String, message: String, dateTime: LocalDateTime): CustomReminder {
            val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            return CustomReminder(
                title = title,
                message = message,
                reminderDateTime = millis
            )
        }
    }
}
