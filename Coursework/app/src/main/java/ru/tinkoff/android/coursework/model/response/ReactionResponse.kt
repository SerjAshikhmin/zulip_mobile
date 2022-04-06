package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class ReactionResponse (
    val code: String = "",
    val msg: String = "",
    val result: String = ""
)
