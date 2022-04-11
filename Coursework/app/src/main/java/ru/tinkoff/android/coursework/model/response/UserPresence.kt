package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class UserPresence (

    @SerialName("status")
    val status: String,

    @SerialName("timestamp")
    val timestamp: Long
)
