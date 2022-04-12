package ru.tinkoff.android.coursework.api.model

internal class EmojiWithCount(
    val code: String,
    val count: Int,
    var selectedByCurrentUser: Boolean = false
)
