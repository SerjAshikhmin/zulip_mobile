package ru.tinkoff.android.coursework.presentation.elm.profile.models

internal sealed class ProfileEffect {

    data class ProfileLoadError(val error: Throwable) : ProfileEffect()

}
