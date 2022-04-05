package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.model.Topic

@Serializable
internal data class TopicsListResponse (
    val topics: List<Topic>
)
