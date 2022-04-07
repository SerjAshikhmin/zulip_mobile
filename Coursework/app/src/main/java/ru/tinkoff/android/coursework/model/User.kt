package ru.tinkoff.android.coursework.model

internal data class User (
    val id: Long,
    val name: String,
    val email: String,
    val status: String,
    val isOnline: Boolean = false
)
