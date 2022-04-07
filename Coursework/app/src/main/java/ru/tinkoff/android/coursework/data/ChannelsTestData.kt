package ru.tinkoff.android.coursework.data

import ru.tinkoff.android.coursework.model.Channel

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
