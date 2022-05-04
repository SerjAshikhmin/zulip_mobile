package ru.tinkoff.android.coursework.presentation.elm.chat

import ru.tinkoff.android.coursework.data.api.ZulipJsonApi.Companion.LAST_MESSAGE_ANCHOR
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatCommand
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEffect
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEvent
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatState
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

internal class ChatReducer : DslReducer<ChatEvent, ChatState, ChatEffect, ChatCommand>() {

    override fun Result.reduce(event: ChatEvent): Any {
        return when (event) {
            is ChatEvent.Ui.InitEvent -> {
                processInitEvent()
            }
            is ChatEvent.Ui.LoadLastMessages -> {
                processLoadLastMessagesEvent(event)
            }
            is ChatEvent.Ui.LoadPortionOfMessages -> {
                processLoadLastMessagesEvent(event)
            }
            is ChatEvent.Ui.SendMessage -> {
                processSendMessageEvent(event)
            }
            is ChatEvent.Ui.AddReaction -> {
                processAddReactionEvent(event)
            }
            is ChatEvent.Ui.RemoveReaction -> {
                processRemoveReactionEvent(event)
            }
            is ChatEvent.Ui.UploadFile -> {
                processFileUploadEvent(event)
            }

            is ChatEvent.Internal.LastMessagesLoaded -> {
                processLastMessagesLoadedEvent(event)
            }
            is ChatEvent.Internal.PortionOfMessagesLoaded -> {
                processPortionOfMessagesLoadedEvent(event)
            }
            is ChatEvent.Internal.MessageSent -> {
                processMessageSentEvent()
            }
            is ChatEvent.Internal.ReactionAdded -> {
                processReactionAddedEvent()
            }
            is ChatEvent.Internal.ReactionRemoved -> {
                processReactionRemovedEvent()
            }
            is ChatEvent.Internal.FileUploaded -> {
                processFileUploadedEvent(event)
            }

            is ChatEvent.Internal.MessagesLoadingError -> {
                processMessagesLoadingError(event)
            }
            is ChatEvent.Internal.MessageSendingError -> {
                processMessageSendingError(event)
            }
            is ChatEvent.Internal.FileUploadingError -> {
                processFileUploadingError(event)
            }
        }
    }

    private fun Result.processInitEvent() {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
    }

    private fun Result.processLoadLastMessagesEvent(event: ChatEvent.Ui.LoadLastMessages) {
        state {
            copy(
                isLoading = true,
                error = null,
                updateAllMessages = event.updateAllMessages,
                updateWithPortion = false,
                isFirstPortion = event.isFirstPortion,
                isReactionAdded = false,
                isReactionRemoved = false,
                topicName = event.topicName
            )
        }
        commands {
            +ChatCommand.LoadLastMessages(
                topicName = event.topicName,
                currentAnchor = event.currentAnchor,
                isFirstPosition = event.isFirstPortion
            )
        }
    }

    private fun Result.processLoadLastMessagesEvent(event: ChatEvent.Ui.LoadPortionOfMessages) {
        state {
            copy(
                isLoading = true,
                error = null,
                updateAllMessages = event.updateAllMessages,
                updateWithPortion = false,
                isFirstPortion = event.isFirstPortion,
                isReactionAdded = false,
                isReactionRemoved = false
            )
        }
        commands {
            +ChatCommand.LoadPortionOfMessages(
                topicName = event.topicName,
                currentAnchor = event.currentAnchor,
                isFirstPosition = event.isFirstPortion
            )
        }
    }

    private fun Result.processSendMessageEvent(event: ChatEvent.Ui.SendMessage) {
        commands {
            +ChatCommand.SendMessage(
                topicName = event.topicName,
                streamName = event.streamName,
                content = event.content
            )
        }
    }

    private fun Result.processAddReactionEvent(event: ChatEvent.Ui.AddReaction) {
        state {
            copy(
                isReactionAdded = false,
                error = null
            )
        }
        commands {
            +ChatCommand.AddReaction(
                messageId = event.messageId,
                emojiName = event.emojiName
            )
        }
    }

    private fun Result.processRemoveReactionEvent(event: ChatEvent.Ui.RemoveReaction) {
        state {
            copy(
                isReactionRemoved = false,
                error = null
            )
        }
        commands {
            +ChatCommand.RemoveReaction(
                messageId = event.messageId,
                emojiName = event.emojiName
            )
        }
    }

    private fun Result.processLastMessagesLoadedEvent(
        event: ChatEvent.Internal.LastMessagesLoaded
    ) {
        state {
            copy(
                items = event.items,
                isLoading = false,
                error = null,
                isFirstPortion = true,
                updateAllMessages = true,
                updateWithPortion = true
            )
        }
    }

    private fun Result.processPortionOfMessagesLoadedEvent(
        event: ChatEvent.Internal.PortionOfMessagesLoaded
    ) {
        state {
            copy(
                items = event.items,
                isLoading = false,
                error = null,
                isFirstPortion = false,
                updateAllMessages = false,
                updateWithPortion = true
            )
        }
    }

    private fun Result.processMessageSentEvent() {
        state {
            copy(
                isReactionAdded = false,
                isReactionRemoved = false,
                updateAllMessages = false,
                updateWithPortion = false,
                isFirstPortion = false,
                error = null
            )
        }
        commands { +ChatCommand.LoadLastMessages(
            topicName = state.topicName,
            currentAnchor = LAST_MESSAGE_ANCHOR,
            isFirstPosition = true
        ) }
        effects { +ChatEffect.MessageSentEffect }
    }

    private fun Result.processFileUploadEvent(
        event: ChatEvent.Ui.UploadFile
    ) {
        commands {
            +ChatCommand.UploadFile(
                event.fileName,
                event.fileBody
            )
        }
    }

    private fun Result.processReactionAddedEvent() {
        state {
            copy(
                isReactionAdded = true,
                isReactionRemoved = false,
                updateAllMessages = false,
                updateWithPortion = false,
                isFirstPortion = false,
                error = null
            )
        }
    }

    private fun Result.processReactionRemovedEvent() {
        state {
            copy(
                isReactionRemoved = true,
                isReactionAdded = false,
                updateAllMessages = false,
                updateWithPortion = false,
                isFirstPortion = false,
                error = null
            )
        }
    }

    private fun Result.processFileUploadedEvent(
        event: ChatEvent.Internal.FileUploaded
    ) {
        state {
            copy(
                isReactionAdded = false,
                isReactionRemoved = false,
                updateAllMessages = false,
                updateWithPortion = false,
                isFirstPortion = false,
                error = null
            )
        }
        effects { +ChatEffect.FileUploadedEffect(event.fileName, event.fileUri) }
    }

    private fun Result.processMessagesLoadingError(
        event: ChatEvent.Internal.MessagesLoadingError
    ) {
        state { copy(error = event.error) }
        effects { +ChatEffect.MessagesLoadingError(event.error) }
    }

    private fun Result.processMessageSendingError(
        event: ChatEvent.Internal.MessageSendingError
    ) {
        state { copy(error = event.error) }
        effects { +ChatEffect.MessageSendingError(event.error) }
    }

    private fun Result.processFileUploadingError(
        event: ChatEvent.Internal.FileUploadingError
    ) {
        state { copy(error = event.error) }
        effects {
            +ChatEffect.FileUploadingError(
                error = event.error,
                fileName = event.fileName,
                fileBody = event.fileBody
            )
        }
    }

}
