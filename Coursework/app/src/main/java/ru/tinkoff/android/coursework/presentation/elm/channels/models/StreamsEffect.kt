package ru.tinkoff.android.coursework.presentation.elm.channels.models

import android.os.Bundle

internal sealed class StreamsEffect {

    data class StreamsListLoadError(val error: Throwable) : StreamsEffect()

    data class NavigateToChat(val bundle: Bundle) : StreamsEffect()

}
