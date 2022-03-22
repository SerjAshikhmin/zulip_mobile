package ru.tinkoff.android.homework_2.data

import ru.tinkoff.android.homework_2.model.User

internal const val SELF_USER_ID = 1L

internal fun getUserById(userId: Long): User? {
    return users.firstOrNull { it.id == userId }
}

internal var users = mutableListOf(
    User(
        id = 1,
        name = "Sergey Ashikhmin",
        email = "ashihmin@yandex.ru",
        status = "Learning Android",
        isOnline = true
    ),
    User(
        id = 2,
        name = "Darrel Steward",
        email = "darrel@company.com",
        status = "In a meeting",
    )
)
