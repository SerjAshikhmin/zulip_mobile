package ru.tinkoff.android.coursework.presentation.elm.people

import io.reactivex.Observable
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleCommand
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleEvent
import vivid.money.elmslie.core.ActorCompat

internal class PeopleActor(
    private val peopleRepository: PeopleRepository
) : ActorCompat<PeopleCommand, PeopleEvent> {

    override fun execute(command: PeopleCommand): Observable<PeopleEvent> = when (command) {
        is PeopleCommand.LoadPeopleListFromDb -> peopleRepository.loadUsersFromDb()
            .mapEvents(
                { list -> PeopleEvent.Internal.PeopleListLoadedFromDb(list) },
                { error -> PeopleEvent.Internal.PeopleListErrorLoading(error) }
            )
        is PeopleCommand.LoadPeopleListFromApi -> peopleRepository.loadUsersFromApi()
            .mapEvents(
                { list -> PeopleEvent.Internal.PeopleListLoadedFromApi(list) },
                { error -> PeopleEvent.Internal.PeopleListErrorLoading(error) }
            )
    }

}
