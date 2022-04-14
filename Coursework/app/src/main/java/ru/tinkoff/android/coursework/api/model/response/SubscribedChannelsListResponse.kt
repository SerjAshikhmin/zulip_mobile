package ru.tinkoff.android.coursework.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.api.model.ChannelDto

@Serializable
internal class SubscribedChannelsListResponse (

    @SerialName("subscriptions")
    val subscriptions: List<ChannelDto>
)
