package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.model.Message

@Serializable
internal data class MessagesListResponse (
    val messages: List<Message>
)
