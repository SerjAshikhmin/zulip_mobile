package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.model.Channel

@Serializable
internal class AllChannelsListResponse (

    @SerialName("streams")
    val streams: List<Channel>
)
