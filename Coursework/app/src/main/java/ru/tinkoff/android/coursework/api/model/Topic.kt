package ru.tinkoff.android.coursework.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Topic (

    @SerialName("name")
    val name: String,
)
