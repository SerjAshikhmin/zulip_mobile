package ru.tinkoff.android.coursework.data

import io.reactivex.Single
import ru.tinkoff.android.coursework.domain.model.Stream

internal interface StreamsRepository {

    fun loadStreamsFromDb(): Single<List<Stream>>
    fun loadStreamsFromApi(isSubscribedStreams: Boolean): Single<List<Stream>>
    fun saveStreamsToDb(streams: List<Stream>)

}
