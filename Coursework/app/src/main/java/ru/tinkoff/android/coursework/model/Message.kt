package ru.tinkoff.android.coursework.model

import java.time.LocalDateTime

internal data class Message (
    val id: Long,
    val userId: Long,
    val topicName: String,
    val content: String,
    val reactions: List<Reaction>,
    val sendDateTime: LocalDateTime
)
