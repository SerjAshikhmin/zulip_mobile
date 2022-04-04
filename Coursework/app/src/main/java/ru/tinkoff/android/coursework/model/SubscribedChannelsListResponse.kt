package ru.tinkoff.android.coursework.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SubscribedChannelsListResponse (
    val subscriptions: List<Channel>
)
