package ru.tinkoff.android.coursework.data

import ru.tinkoff.android.coursework.model.User
import kotlin.random.Random

internal const val SELF_USER_ID = 1L

internal fun getUserById(userId: Long): User? {
    return users.firstOrNull { it.id == userId }
}

// метод-обертка для выбрасывания ошибки и задержки
internal fun usersWithTestError(): MutableList<User> {
    Thread.sleep(1000)
    if (Random.nextBoolean()) throw Exception()
    return users
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
