package ru.tinkoff.android.coursework.presentation.elm.profile

import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileState
import vivid.money.elmslie.core.ElmStoreCompat
import javax.inject.Inject

internal class ProfileElmStoreFactory @Inject constructor(
    private val actor: ProfileActor
) {

    private val store by lazy {
        ElmStoreCompat(
            initialState = ProfileState(),
            reducer = ProfileReducer(),
            actor = actor
        )
    }

    fun provide() = store

}
