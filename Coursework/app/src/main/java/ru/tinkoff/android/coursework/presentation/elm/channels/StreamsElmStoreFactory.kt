package ru.tinkoff.android.coursework.presentation.elm.channels

import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsState
import vivid.money.elmslie.core.ElmStoreCompat

internal class StreamsElmStoreFactory(
    private val actor: StreamsActor
) {

    fun provide() = ElmStoreCompat(
        initialState = StreamsState(),
        reducer = StreamsReducer(),
        actor = actor
    )

}
