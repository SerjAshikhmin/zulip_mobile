package ru.tinkoff.android.coursework.domain.model

internal data class EmojiWithCount(
    val code: String,
    val count: Int,
    var selectedByCurrentUser: Boolean = false
)
