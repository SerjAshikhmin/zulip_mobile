package ru.tinkoff.android.coursework.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Reaction (

    @SerialName("user_id")
    val userId: Long,

    @SerialName("emoji_name")
    val name: String,

    @SerialName("emoji_code")
    val code: String
)
