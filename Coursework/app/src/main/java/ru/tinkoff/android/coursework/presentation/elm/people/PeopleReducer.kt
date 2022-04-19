package ru.tinkoff.android.coursework.presentation.elm.people

import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleCommand
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleEffect
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleEvent
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleState
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

internal class PeopleReducer : DslReducer<PeopleEvent, PeopleState, PeopleEffect, PeopleCommand>() {

    override fun Result.reduce(event: PeopleEvent): Any {
        return when (event) {
            is PeopleEvent.Ui.LoadPeopleList -> {
                state { copy(isLoading = true, error = null, isEmptyState = false) }
                commands { +PeopleCommand.LoadPeopleListFromDb }
            }
            is PeopleEvent.Ui.LoadProfile -> {
                state { copy(isLoading = false, error = null, isEmptyState = false) }
                effects { +PeopleEffect.NavigateToProfile(event.bundle) }
            }
            is PeopleEvent.Internal.PeopleListLoadedFromDb -> {
                val itemsList = event.items
                state { copy(items = itemsList, isLoading = false, error = null, isEmptyState = itemsList.isEmpty()) }
                commands { +PeopleCommand.LoadPeopleListFromApi }
            }
            is PeopleEvent.Internal.PeopleListLoadedFromApi -> {
                val itemsList = event.items
                state { copy(items = itemsList, isLoading = false, error = null, isEmptyState = itemsList.isEmpty()) }
            }
            is PeopleEvent.Internal.PeopleListErrorLoading -> {
                state { copy(error = event.error, isLoading = false, isEmptyState = false) }
                effects { +PeopleEffect.PeopleListLoadError(event.error) }
            }
        }
    }

}
