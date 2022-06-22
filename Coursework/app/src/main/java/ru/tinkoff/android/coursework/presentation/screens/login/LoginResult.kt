package ru.tinkoff.android.coursework.presentation.screens.login

/**
 * Authentication result : success (user details) or error message.
 */
internal data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)
