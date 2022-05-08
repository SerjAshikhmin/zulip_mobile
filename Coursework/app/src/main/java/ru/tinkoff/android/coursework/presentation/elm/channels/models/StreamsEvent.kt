package ru.tinkoff.android.coursework.presentation.elm.channels.models

import android.os.Bundle
import ru.tinkoff.android.coursework.domain.model.Stream

internal sealed class StreamsEvent {

    sealed class Ui : StreamsEvent() {

        object InitEvent : Ui()

        object LoadAllStreamsList : Ui()

        object LoadSubscribedStreamsList : Ui()

        data class LoadChat(val bundle: Bundle) : Ui()

        object SubscribeOnSearchStreamsEvents : Ui()

        data class SearchStreamsByQuery(val query: String) : Ui()

        data class CreateStreamRequest(
            val name: String,
            val description: String,
            val isPrivate: Boolean
        ) : Ui()

        object CreateStreamInit : Ui()

    }

    sealed class Internal : StreamsEvent() {

        data class StreamsListLoaded(val items: List<Stream>) : Internal()

        data class StreamsWithSearchLoaded(val items: List<Stream>) : Internal()

        data class StreamsListLoadingError(val error: Throwable) : Internal()

        object StreamCreated : Internal()

        data class StreamCreationError(val error: Throwable) : Internal()

    }

}
