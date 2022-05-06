package ru.tinkoff.android.coursework.presentation.elm.channels.models

import android.os.Bundle
import ru.tinkoff.android.coursework.domain.model.Stream

internal sealed class StreamsEvent {

    sealed class Ui : StreamsEvent() {

        object LoadAllStreamsList : Ui()

        object LoadSubscribedStreamsList : Ui()

        data class LoadChat(val bundle: Bundle) : Ui()

        object SubscribeOnSearchStreamsEvents : Ui()

        data class SearchStreamsByQuery(val query: String) : Ui()

    }

    sealed class Internal : StreamsEvent() {

        data class StreamsListLoaded(val items: List<Stream>) : Internal()

        data class StreamsWithSearchLoaded(val items: List<Stream>) : Internal()

        data class StreamsListLoadingError(val error: Throwable) : Internal()

    }

}
