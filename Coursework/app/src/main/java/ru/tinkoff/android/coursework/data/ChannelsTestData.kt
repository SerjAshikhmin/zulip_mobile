package ru.tinkoff.android.coursework.data

import io.reactivex.Single
import ru.tinkoff.android.coursework.model.Channel
import kotlin.random.Random

internal fun getChannelsByPartOfName(query: String): Single<List<Channel>> {
    val resultList = if (query.isBlank()) channels else {
        channels.filter { it.name.lowercase().contains(query.lowercase()) }
    }
    return Single.just(resultList)
}

// метод-обертка для выбрасывания ошибки и задержки
internal fun channelsWithTestErrorAndDelay(): List<Channel> {
    Thread.sleep(2000)
    if (Random.nextBoolean()) throw Exception()
    return channels
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
