package ru.tinkoff.android.coursework.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.db.model.Channel

@Serializable
internal data class ChannelDto (

    @SerialName("stream_id")
    val streamId: Long,

    @SerialName("name")
    val name: String,

    var topics: List<TopicDto> = listOf()
) {

    fun toChannelDb(): Channel {
        return Channel(
            streamId = streamId,
            name = name,
            topics = topics
        )
    }

}
