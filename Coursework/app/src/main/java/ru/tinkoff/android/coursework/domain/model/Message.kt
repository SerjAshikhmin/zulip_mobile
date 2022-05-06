package ru.tinkoff.android.coursework.domain.model

internal data class Message(
    val id: Long = 0,
    val userId: Long,
    val userFullName: String,
    val topicName: String,
    val avatarUrl: String?,
    val content: String,
    val emojis: MutableList<EmojiWithCount>,
    val timestamp: Long
)
