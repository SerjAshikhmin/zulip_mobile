package ru.tinkoff.android.coursework.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.api.model.TopicDto

@Serializable
internal class TopicsListResponse (

    @SerialName("topics")
    val topics: List<TopicDto>
)
