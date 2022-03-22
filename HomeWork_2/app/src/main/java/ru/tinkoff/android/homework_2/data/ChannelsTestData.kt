package ru.tinkoff.android.homework_2.data

import ru.tinkoff.android.homework_2.model.Channel

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
