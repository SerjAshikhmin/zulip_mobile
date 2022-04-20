package ru.tinkoff.android.coursework.presentation.elm.channels.models

internal sealed class StreamsCommand {

    data class LoadStreamsListFromApi(val isSubscribedStreams: Boolean = false) : StreamsCommand()

    data class LoadStreamsListFromDb(val isSubscribedStreams: Boolean = false) : StreamsCommand()

}
