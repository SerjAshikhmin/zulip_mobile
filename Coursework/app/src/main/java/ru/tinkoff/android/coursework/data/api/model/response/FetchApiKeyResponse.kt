package ru.tinkoff.android.coursework.data.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class FetchApiKeyResponse (

    @SerialName("result")
    val result: String,

    @SerialName("msg")
    val msg: String,

    @SerialName("api_key")
    val apiKey: String,

    @SerialName("email")
    val email: String
)
