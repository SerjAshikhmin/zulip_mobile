package ru.tinkoff.android.coursework.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Channel (

    @SerialName("stream_id")
    val id: Long,

    val name: String
)
