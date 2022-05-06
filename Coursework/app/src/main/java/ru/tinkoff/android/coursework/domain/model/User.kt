package ru.tinkoff.android.coursework.domain.model

internal data class User (
    val userId: Long,
    val fullName: String? = "",
    val email: String? = "",
    val avatarUrl: String?,
    var presence: String? = "undefined"
)
