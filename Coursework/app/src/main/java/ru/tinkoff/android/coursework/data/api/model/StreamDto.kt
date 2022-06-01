package ru.tinkoff.android.coursework.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class StreamDto (

    @SerialName("stream_id")
    val streamId: Long,

    @SerialName("name")
    val name: String,

    var topics: List<TopicDto> = listOf(),
    var isSubscribed: Boolean = false
)
