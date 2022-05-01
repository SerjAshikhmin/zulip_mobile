package ru.tinkoff.android.coursework.presentation.elm.chat

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
            is ChatEvent.Ui.LoadMessages -> {
                processLoadMessagesEvent(event)
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
                commands { +ChatCommand.UploadFile(event.fileBody) }
            }

            is ChatEvent.Internal.MessagesLoaded -> {
                processMessagesLoadedEvent(event)
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
                state { copy(error = event.error) }
                effects { +ChatEffect.MessagesLoadingError(event.error) }
            }
            is ChatEvent.Internal.MessageSendingError -> {
                state { copy(error = event.error) }
                effects { +ChatEffect.MessageSendingError(event.error) }
            }
            is ChatEvent.Internal.FileUploadingError -> {
                state { copy(error = event.error) }
                effects { +ChatEffect.FileUploadingError(event.error) }
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

    private fun Result.processLoadMessagesEvent(event: ChatEvent.Ui.LoadMessages) {
        state {
            copy(
                isLoading = true,
                error = null,
                updateAllMessages = event.updateAllMessages,
                updateWithPortion = false,
                isFirstPortion = event.isFirstPortion,
                isMessageSent = false,
                isReactionAdded = false,
                isReactionRemoved = false,
                isFileUploaded = false
            )
        }
        commands {
            +ChatCommand.LoadMessages(
                topicName = event.topicName,
                currentAnchor = event.currentAnchor,
                isFirstPosition = event.isFirstPortion,
                updateAllMessages = event.updateAllMessages
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

    private fun Result.processMessagesLoadedEvent(event: ChatEvent.Internal.MessagesLoaded) {
        state {
            copy(
                items = event.items,
                isLoading = false,
                error = null,
                isFirstPortion = event.isFirstPortion,
                updateAllMessages = event.updateAllMessages,
                updateWithPortion = true
            )
        }
    }

    private fun Result.processMessageSentEvent() {
        state {
            copy(
                isMessageSent = true,
                isFileUploaded = false,
                isReactionAdded = false,
                isReactionRemoved = false,
                updateAllMessages = false,
                updateWithPortion = false,
                isFirstPortion = false,
                error = null
            )
        }
    }

    private fun Result.processReactionAddedEvent() {
        state {
            copy(
                isReactionAdded = true,
                isReactionRemoved = false,
                isMessageSent = false,
                isFileUploaded = false,
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
                isMessageSent = false,
                isFileUploaded = false,
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
                isFileUploaded = true,
                fileUri = event.uri,
                isReactionAdded = false,
                isReactionRemoved = false,
                isMessageSent = false,
                updateAllMessages = false,
                updateWithPortion = false,
                isFirstPortion = false,
                error = null
            )
        }
    }

}
