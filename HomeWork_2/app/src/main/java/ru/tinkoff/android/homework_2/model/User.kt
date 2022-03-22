package ru.tinkoff.android.homework_2.model

internal data class User (
    val id: Long,
    val name: String,
    val email: String,
    val status: String,
    val isOnline: Boolean = false
)
