package ru.tinkoff.android.coursework.data

import io.reactivex.Single
import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.data.db.model.Stream

internal interface StreamsRepository {

    fun loadStreamsFromDb(): Single<List<StreamDto>>
    fun loadStreamsFromApi(isSubscribedStreams: Boolean): Single<List<StreamDto>>
    fun saveStreamsToDb(streams: List<Stream>)

}
