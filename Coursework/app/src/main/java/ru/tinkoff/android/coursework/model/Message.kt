package ru.tinkoff.android.coursework.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Message (
    val id: Long,

    @SerialName("sender_id")
    val userId: Long,

    @SerialName("sender_full_name")
    val userFullName: String,

    @SerialName("subject")
    val topicName: String,

    @SerialName("avatar_url")
    val avatarUrl: String?,

    val content: String,
    val reactions: List<Reaction>,
    val timestamp: Long
)
