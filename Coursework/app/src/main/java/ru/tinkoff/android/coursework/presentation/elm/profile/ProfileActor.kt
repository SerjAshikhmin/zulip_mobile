package ru.tinkoff.android.coursework.presentation.elm.profile

import io.reactivex.Observable
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.data.api.model.SELF_USER_ID
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileCommand
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileEvent
import vivid.money.elmslie.core.ActorCompat

internal class ProfileActor(private val peopleRepository: PeopleRepository
) : ActorCompat<ProfileCommand, ProfileEvent> {

    override fun execute(command: ProfileCommand): Observable<ProfileEvent> = when (command) {
        is ProfileCommand.LoadProfileFromDb -> peopleRepository.loadUserFromDb(SELF_USER_ID)
            ?.mapEvents(
                { user -> ProfileEvent.Internal.ProfileLoadedFromDb(listOf(user)) },
                { error -> ProfileEvent.Internal.ProfileErrorLoading(error) }
            ) ?: Observable.empty()
        is ProfileCommand.LoadProfileFromApi -> peopleRepository.loadOwnUserFromApi()
            .mapEvents(
                { user -> ProfileEvent.Internal.ProfileLoadedFromApi(listOf(user)) },
                { error -> ProfileEvent.Internal.ProfileErrorLoading(error) }
            )
        is ProfileCommand.CreateUserFromBundle -> peopleRepository.createUserFromBundle(command.bundle)
            .mapEvents(
                { user -> ProfileEvent.Internal.UserCreatedFromBundle(listOf(user)) },
                { error -> ProfileEvent.Internal.ProfileErrorLoading(error) }
            )
    }

}
