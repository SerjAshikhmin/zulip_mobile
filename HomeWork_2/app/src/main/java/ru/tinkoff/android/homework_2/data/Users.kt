package ru.tinkoff.android.homework_2.data

import ru.tinkoff.android.homework_2.model.User

internal const val SELF_USER_ID = 1L

fun getUserById(userId: Long): User? {
    return users.firstOrNull { it.id == userId }
}

internal var users = mutableListOf(
    User(
        1,
        "Sergey Ashikhmin",
        "ashihmin@yandex.ru",
        "Learning Android",
        true
    ),
    User(
        2,
        "Darrel Steward",
        "darrel@company.com",
        "In a meeting",
    )
)