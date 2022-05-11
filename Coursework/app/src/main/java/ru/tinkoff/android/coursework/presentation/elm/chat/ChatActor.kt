package ru.tinkoff.android.coursework.presentation.elm.chat

import io.reactivex.Observable
import ru.tinkoff.android.coursework.domain.chat.ChatInteractor
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatCommand
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEvent
import vivid.money.elmslie.core.ActorCompat

internal class ChatActor(
    private val chatInteractor: ChatInteractor
) : ActorCompat<ChatCommand, ChatEvent> {

    override fun execute(command: ChatCommand): Observable<ChatEvent> = when (command) {
        is ChatCommand.LoadLastMessages ->
            chatInteractor.loadLastMessages(
                command.streamName,
                command.topicName,
                command.anchor
            )
                .mapEvents(
                    { messages -> ChatEvent.Internal.LastMessagesLoaded(
                        items = messages,
                        topicName = command.topicName
                    ) },
                    { error -> ChatEvent.Internal.MessagesLoadingError(error) }
                )
        is ChatCommand.LoadPortionOfMessages ->
            chatInteractor.loadPortionOfMessages(
                command.streamName,
                command.topicName,
                command.anchor
            )
                .mapEvents(
                    { messages -> ChatEvent.Internal.PortionOfMessagesLoaded(
                        items = messages,
                        topicName = command.topicName
                    ) },
                    { error -> ChatEvent.Internal.MessagesLoadingError(error) }
                )
        is ChatCommand.LoadMessage ->
            chatInteractor.loadMessage(command.messageId)
                .mapEvents(
                    { message -> ChatEvent.Internal.MessageLoaded(message) },
                    { error -> ChatEvent.Internal.MessagesLoadingError(error) }
                )
        is ChatCommand.SendMessage -> {
            chatInteractor.sendMessage(
                topic = command.topicName,
                stream = command.streamName,
                content = command.content
            )
                .mapEvents(
                    { ChatEvent.Internal.MessageSent },
                    { error -> ChatEvent.Internal.MessageSendingError(error) }
                )
        }
        is ChatCommand.AddReaction -> chatInteractor.addReaction(
            messageId = command.messageId,
            emojiName = command.emojiName
        )
            .mapSuccessEvent {
                ChatEvent.Internal.ReactionAdded(command.messageId)
            }
        is ChatCommand.RemoveReaction -> chatInteractor.removeReaction(
            messageId = command.messageId,
            emojiName = command.emojiName
        )
            .mapSuccessEvent{
                ChatEvent.Internal.ReactionRemoved(command.messageId)
            }
        is ChatCommand.UploadFile -> chatInteractor.uploadFile(fileBody = command.fileBody)
            .mapEvents(
                { response -> ChatEvent.Internal.FileUploaded(
                    command.fileName,
                    response.uri
                ) },
                { error -> ChatEvent.Internal.FileUploadingError(
                    error,
                    command.fileName,
                    command.fileBody
                ) }
            )
        is ChatCommand.DeleteMessage -> chatInteractor.deleteMessage(command.messageId)
            .mapEvents(
                { ChatEvent.Internal.MessageDeleted(command.messageId) },
                { error -> ChatEvent.Internal.MessageDeletingError(error) }
            )
    }

}
