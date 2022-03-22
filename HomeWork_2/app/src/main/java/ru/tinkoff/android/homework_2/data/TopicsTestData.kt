package ru.tinkoff.android.homework_2.data

import ru.tinkoff.android.homework_2.R
import ru.tinkoff.android.homework_2.model.Topic

internal var topics = listOf(
    Topic(
        name = "Testing",
        channelName = "general",
        color = R.color.teal_500,
        messages = messages
    ),
    Topic(
        name = "Bruh",
        channelName = "general",
        color = R.color.yellow_600,
        messages = mutableListOf()
    )
)
