package ru.tinkoff.android.coursework.presentation.elm.channels.models

internal sealed class StreamsCommand {

    data class LoadStreamsList(val isSubscribedStreams: Boolean = false) : StreamsCommand()

    data class UpdateStreamsList(val isSubscribedStreams: Boolean = false) : StreamsCommand()

    object SubscribeOnSearchStreamsEvents : StreamsCommand()

    data class SearchStreamsByQuery(val query: String) : StreamsCommand()

    data class CreateStream(
        val name: String,
        val description: String,
        val isPrivate: Boolean
    ) : StreamsCommand()

}
