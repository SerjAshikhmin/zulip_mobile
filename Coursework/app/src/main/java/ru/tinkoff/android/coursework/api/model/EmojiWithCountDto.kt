package ru.tinkoff.android.coursework.api.model

import kotlinx.serialization.Serializable

@Serializable
internal class EmojiWithCountDto(
    val code: String,
    val count: Int,
    var selectedByCurrentUser: Boolean = false
)
