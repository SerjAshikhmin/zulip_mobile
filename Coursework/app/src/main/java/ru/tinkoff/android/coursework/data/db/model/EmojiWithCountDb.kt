package ru.tinkoff.android.coursework.data.db.model

import kotlinx.serialization.Serializable

@Serializable
internal class EmojiWithCountDb(
    val code: String,
    val count: Int,
    var selectedByCurrentUser: Boolean = false
)
