package ru.tinkoff.android.coursework.data

import ru.tinkoff.android.coursework.model.User

internal const val SELF_USER_ID = 1L

internal fun getUserById(userId: Long): User? {
    return users.firstOrNull { it.id == userId }
}

internal var users = mutableListOf(
    User(
        id = 1,
        name = "Сергей Ашихмин"
    ),
    User(
        id = 2,
        name = "Дмитрий Макаров"
    )
)
