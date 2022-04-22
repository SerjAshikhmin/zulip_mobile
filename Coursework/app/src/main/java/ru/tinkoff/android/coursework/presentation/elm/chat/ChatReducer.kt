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
                state { copy(isLoading = true, error = null) }
            }
            is ChatEvent.Ui.LoadMessages -> {
                if (event.isFirstPortion) {
                    state { copy(
                        isLoading = true,
                        error = null,
                        updateAllMessages = true,
                        updateWithPortion = false,
                        isFirstPortion = true,
                        isMessageSent = false,
                        isReactionAdded = false,
                        isReactionRemoved = false,
                        isFileUploaded = false
                    ) }
                    commands { +ChatCommand.LoadMessagesFromDb(event.topicName, event.adapterAnchor) }
                } else {
                    state { copy(
                        isLoading = true,
                        error = null,
                        updateAllMessages = false,
                        updateWithPortion = false,
                        isFirstPortion = false,
                        isMessageSent = false,
                        isReactionAdded = false,
                        isReactionRemoved = false,
                        isFileUploaded = false
                    ) }
                    commands { +ChatCommand.LoadMessagesFromApi(event.topicName, event.adapterAnchor) }
                }
            }
            is ChatEvent.Ui.CacheMessages -> {
                state { copy(isLoading = false, error = null) }
                commands { +ChatCommand.CacheMessages(event.topicName, event.newMessages, event.adapterMessages) }
            }
            is ChatEvent.Ui.SendMessage -> {
                commands { +ChatCommand.SendMessage(event.topicName, event.streamName, event.content) }
            }
            is ChatEvent.Ui.AddReaction -> {
                state { copy(isReactionAdded = false, error = null) }
                commands { +ChatCommand.AddReaction(event.messageId, event.emojiName) }
            }
            is ChatEvent.Ui.RemoveReaction -> {
                state { copy(isReactionRemoved = false, error = null) }
                commands { +ChatCommand.RemoveReaction(event.messageId, event.emojiName) }
            }
            is ChatEvent.Ui.UploadFile -> {
                commands { +ChatCommand.UploadFile(event.fileBody) }
            }

            is ChatEvent.Internal.MessagesLoadedFromDb -> {
                state { copy(items = event.items, isLoading = false, error = null, updateAllMessages = false, updateWithPortion = false) }
                commands { +ChatCommand.LoadMessagesFromApi(event.topicName, event.adapterAnchor) }
            }
            is ChatEvent.Internal.MessagesLoadedFromApi -> {
                state { copy(items = event.items, isLoading = false, error = null, updateWithPortion = true) }
            }
            is ChatEvent.Internal.MessageSent -> {
                state { copy(isMessageSent = true, isFileUploaded = false, isReactionAdded = false, isReactionRemoved = false, error = null) }
            }
            is ChatEvent.Internal.ReactionAdded -> {
                state { copy(isReactionAdded = true, isReactionRemoved = false, isMessageSent = false, isFileUploaded = false, error = null) }
            }
            is ChatEvent.Internal.ReactionRemoved -> {
                state { copy(isReactionRemoved = true, isReactionAdded = false, isMessageSent = false, isFileUploaded = false, error = null) }
            }
            is ChatEvent.Internal.FileUploaded -> {
                state { copy(isFileUploaded = true, fileUri = event.uri, isReactionAdded = false, isReactionRemoved = false, isMessageSent = false, error = null) }
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
