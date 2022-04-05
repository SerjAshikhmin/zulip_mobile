package ru.tinkoff.android.coursework.testdata

import ru.tinkoff.android.coursework.model.User
import kotlin.random.Random

internal const val SELF_USER_ID = 1L

internal fun getUserById(userId: Long): User? {
    return usersTestData.firstOrNull { it.id == userId }
}

// метод-обертка для выбрасывания ошибки и задержки
internal fun usersWithTestErrorAndDelay(): MutableList<User> {
    Thread.sleep(2000)
    if (Random.nextBoolean()) throw Exception()
    return usersTestData
}

internal var usersTestData = mutableListOf(
    User(
        id = 1,
        name = "Sergey Ashikhmin",
        email = "ashihmin@yandex.ru",
        avatarUrl = ""
        //status = "Learning Android",
        //isOnline = true
    ),
    User(
        id = 2,
        name = "Darrel Steward",
        email = "darrel@company.com",
        avatarUrl = ""
        //status = "In a meeting",
    )
)
