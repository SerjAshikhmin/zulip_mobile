package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class UserPresenceResponse (
    val presence: UserPresenceValues
)
