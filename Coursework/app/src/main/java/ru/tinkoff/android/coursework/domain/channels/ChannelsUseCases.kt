package ru.tinkoff.android.coursework.domain.channels

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tinkoff.android.coursework.data.StreamsRepository
import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.presentation.screens.ChannelsFragment
import java.util.concurrent.TimeUnit

internal class ChannelsUseCases(
    private val streamsRepository: StreamsRepository
) {

    private val queryEvents: PublishSubject<String> = PublishSubject.create()

    fun loadStreams(isSubscribedStreams: Boolean): Observable<List<StreamDto>> {
        return Observable.merge(
            streamsRepository.loadStreamsFromDb(),
            streamsRepository.loadStreamsFromApi(isSubscribedStreams)
        )
    }

    fun processSearchQuery(query: String) = queryEvents.onNext(query)

    fun subscribeOnSearchStreamsEvents(): Observable<List<StreamDto>> {
        return queryEvents
            .map { query -> query.trim() }
            .distinctUntilChanged()
            .debounce(ChannelsFragment.DELAY_BETWEEN_ENTERING_CHARACTERS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .flatMap { query ->
                searchStreamsByQuery(query)
            }
    }

    private fun searchStreamsByQuery(query: String): Observable<List<StreamDto>> {
        return loadStreams(false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.filter { stream ->
                    stream.name.lowercase().contains(query.lowercase())
                }
            }
    }

}
