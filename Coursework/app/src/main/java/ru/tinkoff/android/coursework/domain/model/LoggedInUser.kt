package ru.tinkoff.android.coursework.domain.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
internal data class LoggedInUser(
    val userName: String,
    val apiKey: String
)
