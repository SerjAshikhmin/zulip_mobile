package ru.tinkoff.android.coursework.presentation.elm.profile.models

import android.os.Bundle

internal sealed class ProfileCommand {

    object LoadProfileFromApi : ProfileCommand()

    object LoadProfileFromDb : ProfileCommand()

    data class CreateUserFromBundle(val bundle: Bundle) : ProfileCommand()

}
