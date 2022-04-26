package ru.tinkoff.android.coursework.presentation.elm.channels.models

internal sealed class StreamsCommand {

    data class LoadStreamsList(val isSubscribedStreams: Boolean = false) : StreamsCommand()

    object SubscribeOnSearchStreamsEvents : StreamsCommand()

    data class SearchStreamsByQuery(val query: String) : StreamsCommand()

}
