package ru.tinkoff.android.coursework.db.model

internal class EmojiWithCount(
    val code: String,
    val count: Int,
    var selectedByCurrentUser: Boolean = false
)
