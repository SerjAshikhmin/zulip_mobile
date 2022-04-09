package ru.tinkoff.android.coursework.data

import io.reactivex.Single
import ru.tinkoff.android.coursework.model.Channel
import java.util.concurrent.TimeUnit
import kotlin.random.Random

// метод-обертка для выбрасывания ошибки и задержки
internal fun channelsWithTestErrorAndDelay(): Single<List<Channel>> {
    return Single.fromCallable {
        if (Random.nextBoolean()) throw Exception()
        channelsTestData
    }
        .delay(2000, TimeUnit.MILLISECONDS, true)
}

internal var channelsTestData = listOf(
    Channel(
        name = "general",
        topics = topicsTestData
    ),
    Channel(
        name = "Development",
        topics = listOf()
    ),
    Channel(
        name = "Design",
        topics = listOf()
    ),
    Channel(
        name = "HR",
        topics = listOf()
    )
)
