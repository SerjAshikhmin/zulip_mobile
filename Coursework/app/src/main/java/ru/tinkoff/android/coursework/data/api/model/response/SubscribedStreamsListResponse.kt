package ru.tinkoff.android.coursework.data.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.data.api.model.StreamDto

@Serializable
internal class SubscribedStreamsListResponse (

    @SerialName("subscriptions")
    val subscriptions: List<StreamDto>
)
