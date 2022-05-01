package ru.tinkoff.android.coursework.presentation.elm.profile

import io.reactivex.Observable
import ru.tinkoff.android.coursework.domain.profile.ProfileInteractor
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileCommand
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileEvent
import vivid.money.elmslie.core.ActorCompat

internal class ProfileActor (
    private val profileInteractor: ProfileInteractor
) : ActorCompat<ProfileCommand, ProfileEvent> {

    override fun execute(command: ProfileCommand): Observable<ProfileEvent> = when (command) {
        is ProfileCommand.LoadOwnProfile -> profileInteractor.loadOwnUser()
            .mapEvents(
                { user -> ProfileEvent.Internal.ProfileLoaded(listOf(user)) },
                { error -> ProfileEvent.Internal.ProfileLoadingError(error) }
            )
        is ProfileCommand.CreateUserFromBundle ->
            profileInteractor.createUserFromBundle(command.bundle)
                .mapEvents(
                    { user -> ProfileEvent.Internal.UserCreatedFromBundle(listOf(user)) },
                    { error -> ProfileEvent.Internal.ProfileLoadingError(error) }
                )
    }

}
