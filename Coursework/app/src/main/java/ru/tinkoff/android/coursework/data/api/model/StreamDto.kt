package ru.tinkoff.android.coursework.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.data.db.model.Stream

@Serializable
internal data class StreamDto (

    @SerialName("stream_id")
    val streamId: Long,

    @SerialName("name")
    val name: String,

    var topics: List<TopicDto> = listOf()
) {

    fun toStreamDb(): Stream {
        return Stream(
            streamId = streamId,
            name = name,
            topics = topics
        )
    }

}

internal fun List<StreamDto>.toStreamsDbList(): List<Stream> = map { stream -> stream.toStreamDb()}
