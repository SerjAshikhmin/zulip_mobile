package ru.tinkoff.android.coursework.presentation.elm.channels

import io.reactivex.Observable
import ru.tinkoff.android.coursework.domain.channels.ChannelsInteractor
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsCommand
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import vivid.money.elmslie.core.ActorCompat

internal class StreamsActor(
    private val channelsInteractor: ChannelsInteractor
) : ActorCompat<StreamsCommand, StreamsEvent> {

    override fun execute(command: StreamsCommand): Observable<StreamsEvent> = when (command) {
        is StreamsCommand.LoadStreamsList ->
            channelsInteractor.loadStreams(command.isSubscribedStreams)
                .mapEvents(
                    { streams -> StreamsEvent.Internal.StreamsListLoaded(items = streams) },
                    { error -> StreamsEvent.Internal.StreamsListLoadingError(error) }
                )
        is StreamsCommand.UpdateStreamsList ->
            channelsInteractor.updateStreams(command.isSubscribedStreams)
                .mapEvents(
                    { streams -> StreamsEvent.Internal.StreamsListLoaded(items = streams) },
                    { error -> StreamsEvent.Internal.StreamsListLoadingError(error) }
                )
        is StreamsCommand.SubscribeOnSearchStreamsEvents ->
            channelsInteractor.subscribeOnSearchStreamsEvents()
                .mapEvents(
                    { streams -> StreamsEvent.Internal.StreamsWithSearchLoaded(items = streams) },
                    { error -> StreamsEvent.Internal.StreamsListLoadingError(error) }
                )
        is StreamsCommand.SearchStreamsByQuery -> {
            channelsInteractor.processSearchQuery(command.query)
            Observable.empty()
        }
        is StreamsCommand.CreateStream ->
            channelsInteractor.createStream(
                name = command.name,
                description = command.description,
                isPrivate = command.isPrivate
            )
                .mapEvents(
                    { StreamsEvent.Internal.StreamCreated },
                    { error -> StreamsEvent.Internal.StreamCreationError(error) }
                )
    }

}
