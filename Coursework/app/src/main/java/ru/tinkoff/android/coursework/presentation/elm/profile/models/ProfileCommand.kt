package ru.tinkoff.android.coursework.presentation.elm.profile.models

import android.os.Bundle

internal sealed class ProfileCommand {

    object LoadOwnProfile : ProfileCommand()

    data class CreateUserFromBundle(val bundle: Bundle) : ProfileCommand()

}
