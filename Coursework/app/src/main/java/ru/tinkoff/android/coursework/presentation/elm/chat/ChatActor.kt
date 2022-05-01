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
        is ChatCommand.LoadMessages ->
            chatInteractor.loadMessages(
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
                ChatEvent.Internal.ReactionAdded
            }
        is ChatCommand.RemoveReaction -> chatInteractor.removeReaction(
            messageId = command.messageId,
            emojiName = command.emojiName
        )
            .mapSuccessEvent{
                ChatEvent.Internal.ReactionRemoved
            }
        is ChatCommand.UploadFile -> chatInteractor.uploadFile(fileBody = command.fileBody)
            .mapEvents(
                { response -> ChatEvent.Internal.FileUploaded(response.uri) },
                { error -> ChatEvent.Internal.FileUploadingError(error) }
            )
    }

}
