package ru.tinkoff.android.coursework.db.model

internal class User (

    val userId: Long,

    val fullName: String? = "",

    val email: String? = "",

    val avatarUrl: String?,

    var presence: String? = "undefined"
)
