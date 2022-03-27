package ru.tinkoff.android.coursework.data

import io.reactivex.Single
import ru.tinkoff.android.coursework.model.Channel

internal fun getChannelsByPartOfName(query: String): Single<List<Channel>> {
    val resultList = if (query.isBlank()) channels else {
        channels.filter { it.name.lowercase().contains(query.lowercase()) }
    }
    return Single.just(resultList)
}

internal var channels = listOf(
    Channel(
        name = "general",
        topics = topics
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
