package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class SendMessageResponse (
    val id: Int,
    val msg: String,
    val result: String
)
