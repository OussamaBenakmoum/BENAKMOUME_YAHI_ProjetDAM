package com.example.benakmoume_yahi.utils

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun formatTimeDifference(createdAtStr: String): String {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    val createdAt = LocalDateTime.parse(createdAtStr.substringBeforeLast('Z'), formatter)
    val now = LocalDateTime.now()

    val duration = Duration.between(createdAt, now)

    return if (duration.toHours() < 24) {
        val hours = duration.toHours()
        if (hours <= 1) "Il y a 1 heure" else "Il y a $hours heures"
    } else {
        val days = ChronoUnit.DAYS.between(createdAt.toLocalDate(), now.toLocalDate())
        if (days == 1L) "Il y a 1 jour" else "Il y a $days jours"
    }
}
