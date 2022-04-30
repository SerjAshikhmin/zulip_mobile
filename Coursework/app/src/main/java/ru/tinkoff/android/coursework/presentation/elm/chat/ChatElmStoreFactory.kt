package ru.tinkoff.android.coursework.presentation.elm.chat

import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatState
import vivid.money.elmslie.core.ElmStoreCompat

internal class ChatElmStoreFactory(
    private val actor: ChatActor
) {
    private val store by lazy {
        ElmStoreCompat(
            initialState = ChatState(),
            reducer = ChatReducer(),
            actor = actor
        )
    }

    fun provide() = store

}
