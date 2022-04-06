package ru.tinkoff.android.coursework.ui.screens.utils

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

internal fun getDateTimeFromTimestamp(timestamp: Long): LocalDateTime {
    return LocalDateTime.ofInstant(
        Instant.ofEpochSecond(timestamp),
        TimeZone.getDefault().toZoneId()
    )
}
