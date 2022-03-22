package ru.tinkoff.android.homework_2.model

import java.time.LocalDateTime

internal data class Message (
    val id: Long,
    val userId: Long,
    val topicName: String,
    val content: String,
    val reactions: List<Reaction>,
    val sendDateTime: LocalDateTime
)
