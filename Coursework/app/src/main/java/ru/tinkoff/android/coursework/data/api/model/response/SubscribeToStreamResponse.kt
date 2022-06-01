package ru.tinkoff.android.coursework.data.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class SubscribeToStreamResponse (

    @SerialName("msg")
    val msg: String,

    @SerialName("result")
    val result: String
)
