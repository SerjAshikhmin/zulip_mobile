package ru.tinkoff.android.coursework.presentation.elm.people

import io.reactivex.Observable
import ru.tinkoff.android.coursework.domain.people.PeopleInteractor
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleCommand
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleEvent
import vivid.money.elmslie.core.ActorCompat

internal class PeopleActor (
    private val peopleInteractor: PeopleInteractor
) : ActorCompat<PeopleCommand, PeopleEvent> {

    override fun execute(command: PeopleCommand): Observable<PeopleEvent> = when (command) {
        is PeopleCommand.LoadPeopleList -> peopleInteractor.loadUsers()
            .mapEvents(
                { list -> PeopleEvent.Internal.PeopleListLoaded(list) },
                { error -> PeopleEvent.Internal.PeopleListLoadingError(error) }
            )
    }

}
