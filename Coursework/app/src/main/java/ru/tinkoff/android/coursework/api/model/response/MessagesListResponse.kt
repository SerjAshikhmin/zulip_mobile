package ru.tinkoff.android.coursework.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.api.model.Message

@Serializable
internal class MessagesListResponse (

    @SerialName("messages")
    val messages: List<Message>,

    @SerialName("anchor")
    val anchor: Long
)
