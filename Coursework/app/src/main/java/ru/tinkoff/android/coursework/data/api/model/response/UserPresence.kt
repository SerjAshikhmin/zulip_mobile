package ru.tinkoff.android.coursework.data.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class UserPresence (

    @SerialName("status")
    val status: String,

    @SerialName("timestamp")
    val timestamp: Long
)
