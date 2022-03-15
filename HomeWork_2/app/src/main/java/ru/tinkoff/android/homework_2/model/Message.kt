package ru.tinkoff.android.homework_2.model

import java.time.LocalDateTime

class Message (
    val id: Long,
    val userName: String,
    val content: String,
    val reactions: List<Reaction>,
    val sendDateTime: LocalDateTime
)