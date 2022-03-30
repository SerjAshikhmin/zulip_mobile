package ru.tinkoff.android.coursework.data

import ru.tinkoff.android.coursework.model.User

internal const val SELF_USER_ID = 1L

internal fun getUserById(userId: Long): User? {
    return usersTestData.firstOrNull { it.id == userId }
}

internal var usersTestData = mutableListOf(
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
