package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.model.Channel

@Serializable
internal data class AllChannelsListResponse (
    val streams: List<Channel>
)
