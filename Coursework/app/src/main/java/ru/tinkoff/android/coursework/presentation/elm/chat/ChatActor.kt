package ru.tinkoff.android.coursework.presentation.elm.chat

import io.reactivex.Observable
import ru.tinkoff.android.coursework.domain.chat.ChatUseCases
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatCommand
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEvent
import vivid.money.elmslie.core.ActorCompat

internal class ChatActor(
    private val chatUseCases: ChatUseCases
) : ActorCompat<ChatCommand, ChatEvent> {

    override fun execute(command: ChatCommand): Observable<ChatEvent> = when (command) {
        is ChatCommand.LoadMessages ->
            chatUseCases.loadMessages(
                command.topicName,
                command.currentAnchor,
                command.updateAllMessages
            )
                .mapEvents(
                    { messages -> ChatEvent.Internal.MessagesLoaded(
                        items = messages,
                        topicName = command.topicName,
                        isFirstPortion = command.isFirstPosition,
                        updateAllMessages = command.updateAllMessages
                    ) },
                    { error -> ChatEvent.Internal.MessagesLoadingError(error) }
                )
        is ChatCommand.CacheMessages -> {
            chatUseCases.cacheMessages(
                topicName = command.topicName,
                newMessages = command.newMessages,
                actualMessages = command.actualMessages
            )
            Observable.empty()
        }
        is ChatCommand.SendMessage -> {
            chatUseCases.sendMessage(
                topic = command.topicName,
                stream = command.streamName,
                content = command.content
            )
                .mapEvents(
                    { ChatEvent.Internal.MessageSent },
                    { error -> ChatEvent.Internal.MessageSendingError(error) }
                )
        }
        is ChatCommand.AddReaction -> chatUseCases.addReaction(
            messageId = command.messageId,
            emojiName = command.emojiName
        )
            .mapEvents(
                { ChatEvent.Internal.ReactionAdded },
                { error -> ChatEvent.Internal.ReactionAddingError(error) }
            )
        is ChatCommand.RemoveReaction -> chatUseCases.removeReaction(
            messageId = command.messageId,
            emojiName = command.emojiName
        )
            .mapEvents(
                { ChatEvent.Internal.ReactionRemoved },
                { error -> ChatEvent.Internal.ReactionRemovingError(error) }
            )
        is ChatCommand.UploadFile -> chatUseCases.uploadFile(fileBody = command.fileBody)
            .mapEvents(
                { response -> ChatEvent.Internal.FileUploaded(response.uri) },
                { error -> ChatEvent.Internal.FileUploadingError(error) }
            )
    }

}
