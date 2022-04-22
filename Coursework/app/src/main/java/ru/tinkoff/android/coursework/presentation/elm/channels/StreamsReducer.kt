package ru.tinkoff.android.coursework.presentation.elm.channels

import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsCommand
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEffect
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsState
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

internal class StreamsReducer : DslReducer<StreamsEvent, StreamsState, StreamsEffect, StreamsCommand>() {

    override fun Result.reduce(event: StreamsEvent): Any {
        return when (event) {
            is StreamsEvent.Ui.LoadAllStreamsList -> {
                state { copy(isLoading = true, error = null) }
                commands { +StreamsCommand.LoadStreamsListFromDb() }
            }
            is StreamsEvent.Ui.LoadSubscribedStreamsList -> {
                state { copy(isLoading = true, error = null) }
                commands { +StreamsCommand.LoadStreamsListFromDb(isSubscribedStreams = true) }
            }
            is StreamsEvent.Ui.LoadChat-> {
                state { copy(isLoading = false, error = null) }
                effects { +StreamsEffect.NavigateToChat(event.bundle) }
            }
            is StreamsEvent.Internal.StreamsListLoadedFromDb -> {
                state { copy(items = event.items, isLoading = false, error = null) }
                commands { +StreamsCommand.LoadStreamsListFromApi(isSubscribedStreams = event.isSubscribedStreams) }
            }
            is StreamsEvent.Internal.StreamsListLoadedFromApi -> {
                state { copy(items = event.items, isLoading = false, error = null) }
            }
            is StreamsEvent.Internal.StreamsListLoadingError -> {
                state { copy(error = event.error, isLoading = false) }
                effects { +StreamsEffect.StreamsListLoadError(event.error) }
            }
        }
    }

}
