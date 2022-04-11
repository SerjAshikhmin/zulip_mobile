package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class UserPresenceValues (

    @SerialName("aggregated")
    val aggregated: UserPresence? = null
)
