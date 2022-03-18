package ru.tinkoff.android.homework_2.data

import ru.tinkoff.android.homework_2.model.User

internal const val SELF_USER_ID = 1L

fun getUserById(userId: Long): User? {
    return users.firstOrNull { it.id == userId }
}

internal var users = mutableListOf(
    User(
        1,
        "Сергей Ашихмин"
    ),
    User(
        2,
        "Дмитрий Макаров"
    )
)