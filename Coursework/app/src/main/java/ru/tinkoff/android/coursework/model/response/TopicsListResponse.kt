package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.model.Topic

@Serializable
internal class TopicsListResponse (

    @SerialName("topics")
    val topics: List<Topic>
)
