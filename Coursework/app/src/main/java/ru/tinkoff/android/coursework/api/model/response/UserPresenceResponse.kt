package ru.tinkoff.android.coursework.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class UserPresenceResponse (

    @SerialName("presence")
    val presence: UserPresenceValues
)
