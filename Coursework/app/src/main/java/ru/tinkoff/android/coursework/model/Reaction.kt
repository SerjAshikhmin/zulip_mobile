package ru.tinkoff.android.coursework.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class Reaction (

    @SerialName("user_id")
    val userId: Long,

    @SerialName("emoji_name")
    val emojiName: String,

    @SerialName("emoji_code")
    val emojiCode: String
)
