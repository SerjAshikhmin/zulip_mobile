package ru.tinkoff.android.coursework.presentation.elm.profile

import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileCommand
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileEffect
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileEvent
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileState
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

internal class ProfileReducer
    : DslReducer<ProfileEvent, ProfileState, ProfileEffect, ProfileCommand>() {

    override fun Result.reduce(event: ProfileEvent): Any {
        return when (event) {
            is ProfileEvent.Ui.InitEvent -> {
                state {
                    copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
            is ProfileEvent.Ui.LoadOwnProfile -> {
                state {
                    copy(
                        isLoading = true,
                        error = null
                    )
                }
                commands { +ProfileCommand.LoadOwnProfile }
            }
            is ProfileEvent.Ui.LoadProfile -> {
                state {
                    copy(
                        isLoading = true,
                        error = null
                    )
                }
                commands { +ProfileCommand.CreateUserFromBundle(event.bundle) }
            }
            is ProfileEvent.Internal.ProfileLoaded -> {
                state {
                    copy(
                        items = event.items,
                        isLoading = false,
                        error = null
                    )
                }
            }
            is ProfileEvent.Internal.UserCreatedFromBundle -> {
                state {
                    copy(
                        items = event.items,
                        isLoading = false,
                        error = null
                    )
                }
            }
            is ProfileEvent.Internal.ProfileLoadingError -> {
                state {
                    copy(
                        error = event.error,
                        isLoading = false
                    )
                }
                effects { +ProfileEffect.ProfileLoadError(event.error) }
            }
        }
    }

}
