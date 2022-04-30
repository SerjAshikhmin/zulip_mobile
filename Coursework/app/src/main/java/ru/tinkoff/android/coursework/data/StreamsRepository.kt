package ru.tinkoff.android.coursework.data

import io.reactivex.Observable
import ru.tinkoff.android.coursework.data.api.model.StreamDto

internal interface StreamsRepository {

    fun loadStreamsFromDb(): Observable<List<StreamDto>>?
    fun loadStreamsFromApi(isSubscribedStreams: Boolean): Observable<List<StreamDto>>

}
