package ru.tinkoff.android.coursework.presentation.elm.channels.models

import android.os.Bundle
import ru.tinkoff.android.coursework.data.api.model.StreamDto

internal sealed class StreamsEvent {

    sealed class Ui : StreamsEvent() {

        object LoadAllStreamsList : Ui()

        object LoadSubscribedStreamsList : Ui()

        data class LoadChat(val bundle: Bundle) : Ui()
    }

    sealed class Internal : StreamsEvent() {

        data class StreamsListLoadedFromApi(val items: List<StreamDto>) : Internal()

        data class StreamsListLoadedFromDb(val items: List<StreamDto>, val isSubscribedStreams: Boolean = false) : Internal()

        data class StreamsListLoadingError(val error: Throwable) : Internal()
    }

}
