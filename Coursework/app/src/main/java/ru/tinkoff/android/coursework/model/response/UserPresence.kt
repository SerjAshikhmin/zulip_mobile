package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.Serializable

@Serializable
internal data class UserPresence (
    val status: String,
    val timestamp: Long
)
