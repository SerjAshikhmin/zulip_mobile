package ru.tinkoff.android.coursework.domain.interfaces

import io.reactivex.Single
import ru.tinkoff.android.coursework.data.api.model.response.SubscribeToStreamResponse
import ru.tinkoff.android.coursework.domain.model.Stream

internal interface StreamsRepository {

    fun loadStreamsFromDb(isSubscribedStreams: Boolean): Single<List<Stream>>

    fun loadStreamsFromApi(isSubscribedStreams: Boolean): Single<List<Stream>>

    fun saveStreamsToDb(streams: List<Stream>, isSubscribedStreams: Boolean)

    fun createStream(
        name: String,
        description: String,
        isPrivate: Boolean
    ): Single<SubscribeToStreamResponse>

    fun deleteStreamsFromDb(isSubscribedStreams: Boolean)

}
