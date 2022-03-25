package ru.tinkoff.android.coursework.data

import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.model.Topic

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
