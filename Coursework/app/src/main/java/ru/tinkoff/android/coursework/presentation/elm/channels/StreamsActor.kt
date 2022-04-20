package ru.tinkoff.android.coursework.presentation.elm.channels

import io.reactivex.Observable
import ru.tinkoff.android.coursework.data.StreamsRepository
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsCommand
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import vivid.money.elmslie.core.ActorCompat

internal class StreamsActor(
    private val streamsRepository: StreamsRepository
) : ActorCompat<StreamsCommand, StreamsEvent> {

    override fun execute(command: StreamsCommand): Observable<StreamsEvent> = when (command) {
        is StreamsCommand.LoadStreamsListFromDb -> streamsRepository.loadStreamsFromDb()
            ?.mapEvents(
                { streams -> StreamsEvent.Internal.StreamsListLoadedFromDb(items = streams, isSubscribedStreams = command.isSubscribedStreams) },
                { error -> StreamsEvent.Internal.StreamsListErrorLoading(error) }
            ) ?: Observable.empty()
        is StreamsCommand.LoadStreamsListFromApi -> streamsRepository.loadStreamsFromApi(command.isSubscribedStreams)
            .mapEvents(
                { streams -> StreamsEvent.Internal.StreamsListLoadedFromApi(streams) },
                { error -> StreamsEvent.Internal.StreamsListErrorLoading(error) }
            )
    }

}
