package ru.tinkoff.android.coursework.presentation.elm.people

import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleState
import vivid.money.elmslie.core.ElmStoreCompat
import javax.inject.Inject

internal class PeopleElmStoreFactory @Inject constructor(
    private val actor: PeopleActor
) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = PeopleState(),
            reducer = PeopleReducer(),
            actor = actor
        )
    }

    fun provide() = store

}
