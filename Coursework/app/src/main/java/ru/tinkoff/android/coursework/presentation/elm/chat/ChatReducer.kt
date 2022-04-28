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
                state {
                    copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
            is ChatEvent.Ui.LoadMessages -> {
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
                commands { +ChatCommand.LoadMessages(
                    topicName = event.topicName,
                    currentAnchor = event.currentAnchor,
                    isFirstPosition = event.isFirstPortion,
                    updateAllMessages = event.updateAllMessages
                ) }
            }
            is ChatEvent.Ui.SendMessage -> {
                commands { +ChatCommand.SendMessage(
                    topicName = event.topicName,
                    streamName = event.streamName,
                    content = event.content
                ) }
            }
            is ChatEvent.Ui.AddReaction -> {
                state {
                    copy(
                        isReactionAdded = false,
                        error = null
                    )
                }
                commands { +ChatCommand.AddReaction(
                    messageId = event.messageId,
                    emojiName = event.emojiName
                ) }
            }
            is ChatEvent.Ui.RemoveReaction -> {
                state {
                    copy(
                        isReactionRemoved = false,
                        error = null
                    )
                }
                commands { +ChatCommand.RemoveReaction(
                    messageId = event.messageId,
                    emojiName = event.emojiName
                ) }
            }
            is ChatEvent.Ui.UploadFile -> {
                commands { +ChatCommand.UploadFile(event.fileBody) }
            }

            is ChatEvent.Internal.MessagesLoaded -> {
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
                commands { +ChatCommand.CacheMessages(
                    topicName = event.topicName,
                    messages = event.items
                ) }
            }
            is ChatEvent.Internal.MessageSent -> {
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
            is ChatEvent.Internal.ReactionAdded -> {
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
            is ChatEvent.Internal.ReactionRemoved -> {
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
            is ChatEvent.Internal.FileUploaded -> {
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

            is ChatEvent.Internal.MessagesLoadingError -> {
                state { copy(error = event.error) }
                effects { +ChatEffect.MessagesLoadingError(event.error) }
            }
            is ChatEvent.Internal.MessageSendingError -> {
                state { copy(error = event.error) }
                effects { +ChatEffect.MessageSendingError(event.error) }
            }
            is ChatEvent.Internal.ReactionAddingError -> {
                state { copy(error = event.error) }
                effects { +ChatEffect.ReactionAddingError(event.error) }
            }
            is ChatEvent.Internal.ReactionRemovingError -> {
                state { copy(error = event.error) }
                effects { +ChatEffect.ReactionRemovingError(event.error) }
            }
            is ChatEvent.Internal.FileUploadingError -> {
                state { copy(error = event.error) }
                effects { +ChatEffect.FileUploadingError(event.error) }
            }
        }
    }

}
