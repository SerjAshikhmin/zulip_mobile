package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class ReactionResponse (

    @SerialName("code")
    val code: String = "",

    @SerialName("msg")
    val msg: String = "",

    @SerialName("result")
    val result: String = ""
)
