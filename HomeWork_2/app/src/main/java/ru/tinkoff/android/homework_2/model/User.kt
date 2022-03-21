package ru.tinkoff.android.homework_2.model

data class User (
    val id: Long,
    val name: String,
    val email: String,
    val status: String,
    val onlineStatus: Boolean = false
)