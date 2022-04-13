package ru.tinkoff.android.coursework.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Channel (

    @SerialName("stream_id")
    val streamId: Long,

    @SerialName("name")
    val name: String,

    var topics: List<Topic> = listOf()
)
