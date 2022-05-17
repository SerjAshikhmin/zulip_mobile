package ru.tinkoff.android.coursework.domain.channels

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tinkoff.android.coursework.data.StreamsRepository
import ru.tinkoff.android.coursework.data.api.model.response.SubscribeToStreamResponse
import ru.tinkoff.android.coursework.domain.model.Stream
import java.util.concurrent.TimeUnit

internal class ChannelsInteractor(
    private val streamsRepository: StreamsRepository
) {

    private val queryEvents: PublishSubject<String> = PublishSubject.create()

    fun loadStreams(isSubscribedStreams: Boolean): Observable<List<Stream>> {
        return Single.merge(
            streamsRepository.loadStreamsFromDb(isSubscribedStreams),
            streamsRepository.loadStreamsFromApi(isSubscribedStreams)
                .doOnSuccess {
                    if (it.isNotEmpty()) {
                        cacheStreams(it, isSubscribedStreams)
                    }
                }
        ).toObservable()
            .subscribeOn(Schedulers.io())
    }

    fun updateStreams(isSubscribedStreams: Boolean): Observable<List<Stream>> {
        return streamsRepository.loadStreamsFromApi(isSubscribedStreams)
                .doOnSuccess {
                    if (it.isNotEmpty()) {
                        cacheStreams(it, isSubscribedStreams)
                    }
                }
            .toObservable()
            .subscribeOn(Schedulers.io())
    }

    private fun cacheStreams(streams: List<Stream>, isSubscribedStreams: Boolean) {
        if (streams.isNotEmpty()) {
            streamsRepository.deleteStreamsFromDb(isSubscribedStreams)
            streamsRepository.saveStreamsToDb(streams, isSubscribedStreams)
        }
    }

    fun processSearchQuery(query: String) = queryEvents.onNext(query)

    fun subscribeOnSearchStreamsEvents(): Observable<List<Stream>> {
        return queryEvents
            .map { query -> query.trim() }
            .distinctUntilChanged()
            .debounce(DELAY_BETWEEN_ENTERING_CHARACTERS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { query ->
                searchStreamsByQuery(query)
            }
    }

    fun createStream(
        name: String,
        description: String,
        isPrivate: Boolean
    ): Observable<SubscribeToStreamResponse> {
        return streamsRepository.createStream(name, description, isPrivate).toObservable()
    }

    private fun searchStreamsByQuery(query: String): Observable<List<Stream>> {
        return loadStreams(false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.filter { stream ->
                    stream.name.lowercase().contains(query.lowercase())
                }
            }
    }

    companion object {

        const val DELAY_BETWEEN_ENTERING_CHARACTERS = 500L
    }

}
