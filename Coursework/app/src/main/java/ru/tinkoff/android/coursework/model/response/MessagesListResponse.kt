package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.model.Message

@Serializable
internal class MessagesListResponse (

    @SerialName("messages")
    val messages: List<Message>
)
