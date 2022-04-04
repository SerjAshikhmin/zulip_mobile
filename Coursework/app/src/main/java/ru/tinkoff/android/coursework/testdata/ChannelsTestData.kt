package ru.tinkoff.android.coursework.testdata

import io.reactivex.Single
import ru.tinkoff.android.coursework.model.Channel
import kotlin.random.Random

internal fun getChannelsByPartOfName(query: String): Single<List<Channel>> {
    val resultList = if (query.isBlank()) channelsTestData else {
        channelsTestData.filter { it.name.lowercase().contains(query.lowercase()) }
    }
    return Single.just(resultList)
}

// метод-обертка для выбрасывания ошибки и задержки
internal fun channelsWithTestErrorAndDelay(): List<Channel> {
    Thread.sleep(2000)
    if (Random.nextBoolean()) throw Exception()
    return channelsTestData
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
