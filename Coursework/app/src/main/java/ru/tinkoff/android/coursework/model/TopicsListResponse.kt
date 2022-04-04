package ru.tinkoff.android.coursework.model

import kotlinx.serialization.Serializable

@Serializable
internal data class TopicsListResponse (
    val topics: List<Topic>
)
