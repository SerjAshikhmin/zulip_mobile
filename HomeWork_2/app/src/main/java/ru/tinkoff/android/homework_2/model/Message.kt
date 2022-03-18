package ru.tinkoff.android.homework_2.model

import java.time.LocalDateTime

data class Message (
    val id: Long,
    val userId: Long,
    val content: String,
    val reactions: List<Reaction>,
    val sendDateTime: LocalDateTime
)