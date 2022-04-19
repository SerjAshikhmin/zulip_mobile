package ru.tinkoff.android.coursework.data.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class UploadFileResponse (

    @SerialName("msg")
    val msg: String,

    @SerialName("result")
    val result: String,

    @SerialName("uri")
    val uri: String,
)
