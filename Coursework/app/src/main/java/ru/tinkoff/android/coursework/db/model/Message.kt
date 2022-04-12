package ru.tinkoff.android.coursework.db.model

internal class Message (

    val id: Long,

    val userId: Long,

    val userFullName: String,

    val topicName: String,

    val avatarUrl: String?,

    val content: String,

    val reactions: List<Reaction>,

    val timestamp: Long
)
