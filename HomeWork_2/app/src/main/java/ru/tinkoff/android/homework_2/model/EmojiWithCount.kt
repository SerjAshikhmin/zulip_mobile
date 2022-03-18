package ru.tinkoff.android.homework_2.model

class EmojiWithCount(
    val code: String,
    val count: Int,
    var selectedByCurrentUser: Boolean = false
)