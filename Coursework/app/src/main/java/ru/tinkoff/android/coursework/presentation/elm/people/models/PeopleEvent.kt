package ru.tinkoff.android.coursework.presentation.elm.people.models

import android.os.Bundle
import ru.tinkoff.android.coursework.domain.model.User

internal sealed class PeopleEvent {

    sealed class Ui : PeopleEvent() {

        object InitEvent : Ui()

        object LoadPeopleList : Ui()

        object UpdatePeopleList : Ui()

        data class LoadProfile(val bundle: Bundle) : Ui()

    }

    sealed class Internal : PeopleEvent() {

        data class PeopleListLoaded(val items: List<User>) : Internal()

        data class PeopleListLoadingError(val error: Throwable) : Internal()

    }

}
