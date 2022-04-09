package ru.tinkoff.android.coursework.data

import io.reactivex.Single
import ru.tinkoff.android.coursework.model.User
import java.util.concurrent.TimeUnit
import kotlin.random.Random

internal const val SELF_USER_ID = 1L

internal fun getUserById(userId: Long): User? {
    return usersTestData.firstOrNull { it.id == userId }
}

// метод-обертка для выбрасывания ошибки и задержки
internal fun usersWithTestErrorAndDelay(): Single<List<User>> {
    return Single.fromCallable {
        if (Random.nextBoolean()) throw Exception()
        usersTestData
    }
        .delay(2000, TimeUnit.MILLISECONDS, true)
}

internal var usersTestData = listOf(
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
