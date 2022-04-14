package ru.tinkoff.android.coursework.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class ReactionDto (

    @SerialName("user_id")
    val userId: Long,

    @SerialName("emoji_name")
    val emojiName: String,

    @SerialName("emoji_code")
    val emojiCode: String
)
