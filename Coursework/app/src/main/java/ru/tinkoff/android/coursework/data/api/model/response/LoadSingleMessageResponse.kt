package ru.tinkoff.android.coursework.data.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.data.api.model.MessageDto

@Serializable
internal class LoadSingleMessageResponse (

    @SerialName("message")
    val message: MessageDto
)
