package ru.tinkoff.android.coursework.presentation.screens.login

/**
 * Data validation state of the login form.
 */
internal data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
