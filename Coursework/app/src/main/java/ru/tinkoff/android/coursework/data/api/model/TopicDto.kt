package ru.tinkoff.android.coursework.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TopicDto (

    @SerialName("name")
    val name: String,
)
