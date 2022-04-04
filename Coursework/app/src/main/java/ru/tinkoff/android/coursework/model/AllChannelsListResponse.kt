package ru.tinkoff.android.coursework.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AllChannelsListResponse (
    val streams: List<Channel>
)
