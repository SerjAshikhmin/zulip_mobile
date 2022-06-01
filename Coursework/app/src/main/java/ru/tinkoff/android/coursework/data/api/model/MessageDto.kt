package ru.tinkoff.android.coursework.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MessageDto (

    @SerialName("id")
    val id: Long,

    @SerialName("sender_id")
    val userId: Long,

    @SerialName("sender_full_name")
    val userFullName: String,

    @SerialName("stream_id")
    val streamId: Long,

    @SerialName("subject")
    val topicName: String,

    @SerialName("avatar_url")
    val avatarUrl: String?,

    @SerialName("content")
    val content: String,

    @SerialName("reactions")
    val reactions: List<ReactionDto>,

    @SerialName("timestamp")
    val timestamp: Long
)
