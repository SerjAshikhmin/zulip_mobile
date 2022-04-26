package ru.tinkoff.android.coursework.presentation.elm.people.models

import android.os.Bundle
import ru.tinkoff.android.coursework.data.api.model.UserDto

internal sealed class PeopleEvent {

    sealed class Ui : PeopleEvent() {

        object LoadPeopleList : Ui()

        data class LoadProfile(val bundle: Bundle) : Ui()

    }

    sealed class Internal : PeopleEvent() {

        data class PeopleListLoaded(val items: List<UserDto>) : Internal()

        data class PeopleListLoadingError(val error: Throwable) : Internal()

    }

}
