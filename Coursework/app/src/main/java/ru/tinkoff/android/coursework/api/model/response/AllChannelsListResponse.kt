package ru.tinkoff.android.coursework.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.api.model.ChannelDto

@Serializable
internal class AllChannelsListResponse (

    @SerialName("streams")
    val streams: List<ChannelDto>
)
