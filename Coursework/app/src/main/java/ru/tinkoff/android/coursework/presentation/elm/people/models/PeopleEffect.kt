package ru.tinkoff.android.coursework.presentation.elm.people.models

import android.os.Bundle

internal sealed class PeopleEffect {

    data class PeopleListLoadError(val error: Throwable) : PeopleEffect()

    data class NavigateToProfile(val bundle: Bundle) : PeopleEffect()

}
