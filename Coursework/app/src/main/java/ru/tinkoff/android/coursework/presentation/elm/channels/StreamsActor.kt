package ru.tinkoff.android.coursework.presentation.elm.channels

import io.reactivex.Observable
import ru.tinkoff.android.coursework.domain.channels.ChannelsUseCases
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsCommand
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import vivid.money.elmslie.core.ActorCompat

internal class StreamsActor(
    private val channelsUseCases: ChannelsUseCases
) : ActorCompat<StreamsCommand, StreamsEvent> {

    override fun execute(command: StreamsCommand): Observable<StreamsEvent> = when (command) {
        is StreamsCommand.LoadStreamsList ->
            channelsUseCases.loadStreams(command.isSubscribedStreams)
                .mapEvents(
                    { streams -> StreamsEvent.Internal.StreamsListLoaded(items = streams) },
                    { error -> StreamsEvent.Internal.StreamsListLoadingError(error) }
                )
        is StreamsCommand.SubscribeOnSearchStreamsEvents ->
            channelsUseCases.subscribeOnSearchStreamsEvents()
                .mapEvents(
                    { streams -> StreamsEvent.Internal.StreamsWithSearchLoaded(items = streams) },
                    { error -> StreamsEvent.Internal.StreamsListLoadingError(error) }
                )
        is StreamsCommand.SearchStreamsByQuery -> {
            channelsUseCases.processSearchQuery(command.query)
            Observable.empty()
        }
    }

}
