package ru.tinkoff.android.coursework.presentation.elm.channels

import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsCommand
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEffect
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsState
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

internal class StreamsReducer
    : DslReducer<StreamsEvent, StreamsState, StreamsEffect, StreamsCommand>() {

    override fun Result.reduce(event: StreamsEvent): Any {
        return when (event) {
            is StreamsEvent.Ui.InitEvent -> {
                processInitEvent()
            }
            is StreamsEvent.Ui.LoadAllStreamsList -> {
                processLoadAllStreamsListEvent()
            }
            is StreamsEvent.Ui.UpdateAllStreamsList -> {
                processUpdateAllStreamsListEvent()
            }
            is StreamsEvent.Ui.LoadSubscribedStreamsList -> {
                processLoadSubscribedStreamsListEvent()
            }
            is StreamsEvent.Ui.UpdateSubscribedStreamsList -> {
                processUpdateSubscribedStreamsListEvent()
            }
            is StreamsEvent.Ui.LoadChat-> {
                processLoadChatEvent(event)
            }
            is StreamsEvent.Ui.SubscribeOnSearchStreamsEvents -> {
                processSubscribeOnSearchStreamsEvents()
            }
            is StreamsEvent.Ui.SearchStreamsByQuery -> {
                processSearchStreamsByQueryEvent(event)
            }
            is StreamsEvent.Ui.CreateStreamRequest -> {
                processCreateStreamRequest(event)
            }
            is StreamsEvent.Ui.CreateStreamInit -> {
                effects { +StreamsEffect.NavigateToCreateStream }
            }

            is StreamsEvent.Internal.StreamsListLoaded -> {
                processStreamsListLoadedEvent(event)
            }
            is StreamsEvent.Internal.StreamsWithSearchLoaded -> {
                processStreamsWithSearchLoadedEvent(event)
            }
            is StreamsEvent.Internal.StreamCreated -> {
                processStreamCreatedEvent()
            }
            is StreamsEvent.Internal.StreamsListLoadingError -> {
                processStreamsListLoadingError(event)
            }
            is StreamsEvent.Internal.StreamCreationError -> {
                processStreamCreationError(event)
            }
        }
    }

    private fun Result.processInitEvent() {
        state {
            copy(
                isLoading = false,
                error = null
            )
        }
    }

    private fun Result.processLoadAllStreamsListEvent() {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
        commands { +StreamsCommand.LoadStreamsList() }
    }

    private fun Result.processUpdateAllStreamsListEvent() {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
        commands { +StreamsCommand.UpdateStreamsList() }
    }

    private fun Result.processLoadSubscribedStreamsListEvent() {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
        commands { +StreamsCommand.LoadStreamsList(isSubscribedStreams = true) }
    }

    private fun Result.processUpdateSubscribedStreamsListEvent() {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
        commands { +StreamsCommand.UpdateStreamsList(isSubscribedStreams = true) }
    }

    private fun Result.processLoadChatEvent(
        event: StreamsEvent.Ui.LoadChat
    ) {
        state {
            copy(
                isLoading = false,
                error = null
            )
        }
        effects { +StreamsEffect.NavigateToChat(event.bundle) }
    }

    private fun Result.processSubscribeOnSearchStreamsEvents() {
        state {
            copy(
                isLoading = false,
                error = null
            )
        }
        commands { +StreamsCommand.SubscribeOnSearchStreamsEvents }
    }

    private fun Result.processSearchStreamsByQueryEvent(
        event: StreamsEvent.Ui.SearchStreamsByQuery
    ) {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
        commands { +StreamsCommand.SearchStreamsByQuery((event.query)) }
    }

    private fun Result.processCreateStreamRequest(
        event: StreamsEvent.Ui.CreateStreamRequest
    ) {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
        commands {
            +StreamsCommand.CreateStream(
                event.name,
                event.description,
                event.isPrivate
            )
        }
    }

    private fun Result.processStreamsListLoadedEvent(
        event: StreamsEvent.Internal.StreamsListLoaded
    ) {
        state {
            copy(
                items = event.items,
                isLoading = false,
                error = null
            )
        }
    }

    private fun Result.processStreamsWithSearchLoadedEvent(
        event: StreamsEvent.Internal.StreamsWithSearchLoaded
    ) {
        state {
            copy(
                items = event.items,
                isLoading = false,
                error = null
            )
        }
    }

    private fun Result.processStreamCreatedEvent() {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
        effects { +StreamsEffect.StreamCreated }
    }

    private fun Result.processStreamsListLoadingError(
        event: StreamsEvent.Internal.StreamsListLoadingError
    ) {
        state {
            copy(
                error = event.error,
                isLoading = false
            )
        }
        effects { +StreamsEffect.StreamsListLoadError(event.error) }
    }

    private fun Result.processStreamCreationError(
        event: StreamsEvent.Internal.StreamCreationError
    ) {
        state {
            copy(
                error = event.error,
                isLoading = false
            )
        }
        effects { +StreamsEffect.StreamCreationError(event.error) }
    }

}
