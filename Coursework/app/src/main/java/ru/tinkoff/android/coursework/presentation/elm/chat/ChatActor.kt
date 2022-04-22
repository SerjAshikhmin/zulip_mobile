package ru.tinkoff.android.coursework.presentation.elm.chat

import io.reactivex.Observable
import ru.tinkoff.android.coursework.data.ChatRepository
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatCommand
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEvent
import vivid.money.elmslie.core.ActorCompat

internal class ChatActor(
    private val chatRepository: ChatRepository
) : ActorCompat<ChatCommand, ChatEvent> {

    override fun execute(command: ChatCommand): Observable<ChatEvent> = when (command) {
        is ChatCommand.LoadMessagesFromDb -> chatRepository.loadMessagesFromDb(command.topicName)
            ?.mapEvents(
                { messages -> ChatEvent.Internal.MessagesLoadedFromDb(items = messages, topicName = command.topicName, command.adapterAnchor) },
                { error -> ChatEvent.Internal.MessagesLoadingError(error) }
            ) ?: Observable.empty()
        is ChatCommand.LoadMessagesFromApi ->
            chatRepository.loadMessagesFromApi(topicName = command.topicName, adapterAnchor = command.adapterAnchor)
                .mapEvents(
                    { messages -> ChatEvent.Internal.MessagesLoadedFromApi(messages) },
                    { error -> ChatEvent.Internal.MessagesLoadingError(error) }
                )
        is ChatCommand.CacheMessages -> {
            chatRepository.cacheMessages(topicName = command.topicName, newMessages = command.newMessages, adapterMessages = command.adapterMessages)
            Observable.empty()
        }
        is ChatCommand.SendMessage -> {
            chatRepository.sendMessage(topic = command.topicName, stream = command.streamName, content = command.content)
                .mapEvents(
                    { ChatEvent.Internal.MessageSent },
                    { error -> ChatEvent.Internal.MessageSendingError(error) }
                )
        }
        is ChatCommand.AddReaction -> chatRepository.addReaction(messageId = command.messageId, emojiName = command.emojiName)
            .mapEvents(
                { ChatEvent.Internal.ReactionAdded },
                { error -> ChatEvent.Internal.ReactionAddingError(error) }
            )
        is ChatCommand.RemoveReaction -> chatRepository.removeReaction(messageId = command.messageId, emojiName = command.emojiName)
            .mapEvents(
                { ChatEvent.Internal.ReactionRemoved },
                { error -> ChatEvent.Internal.ReactionRemovingError(error) }
            )
        is ChatCommand.UploadFile -> chatRepository.uploadFile(fileBody = command.fileBody)
            .mapEvents(
                { response -> ChatEvent.Internal.FileUploaded(response.uri) },
                { error -> ChatEvent.Internal.FileUploadingError(error) }
            )
    }

}
