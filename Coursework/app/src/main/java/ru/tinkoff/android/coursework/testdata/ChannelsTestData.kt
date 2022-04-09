package ru.tinkoff.android.coursework.testdata

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
        id = 1,
        name = "general",
        //topics = topicsTestData
    ),
    Channel(
        id = 2,
        name = "Development",
        //topics = listOf()
    ),
    Channel(
        id = 3,
        name = "Design",
        //topics = listOf()
    ),
    Channel(
        id = 4,
        name = "HR",
        //topics = listOf()
    )
)
