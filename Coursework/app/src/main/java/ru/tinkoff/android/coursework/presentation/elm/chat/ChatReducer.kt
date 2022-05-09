package ru.tinkoff.android.coursework.presentation.elm.chat

import ru.tinkoff.android.coursework.data.api.ZulipJsonApi.Companion.LAST_MESSAGE_ANCHOR
import ru.tinkoff.android.coursework.domain.model.EmojiWithCount
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatCommand
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEffect
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEvent
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatState
import ru.tinkoff.android.coursework.utils.fromHexToDecimal
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
            is ChatEvent.Ui.LoadChat -> {
                processLoadChatEvent(event)
            }

            is ChatEvent.Internal.LastMessagesLoaded -> {
                processLastMessagesLoadedEvent(event)
            }
            is ChatEvent.Internal.PortionOfMessagesLoaded -> {
                processPortionOfMessagesLoadedEvent(event)
            }
            is ChatEvent.Internal.MessageLoaded -> {
                processLoadMessageEvent(event)
            }
            is ChatEvent.Internal.MessageSent -> {
                processMessageSentEvent()
            }
            is ChatEvent.Internal.ReactionAdded -> {
                processReactionAddedEvent(event)
            }
            is ChatEvent.Internal.ReactionRemoved -> {
                processReactionRemovedEvent(event)
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
                isLoading = false,
                error = null
            )
        }
    }

    private fun Result.processLoadLastMessagesEvent(event: ChatEvent.Ui.LoadLastMessages) {
        state {
            copy(
                isLoading = true,
                error = null,
                streamName = event.streamName,
                topicName = event.topicName
            )
        }
        commands {
            +ChatCommand.LoadLastMessages(
                streamName = state.streamName,
                topicName = state.topicName,
                anchor = event.anchor
            )
        }
    }

    private fun Result.processLoadLastMessagesEvent(event: ChatEvent.Ui.LoadPortionOfMessages) {
        state {
            copy(
                isLoading = true,
                error = null
            )
        }
        commands {
            +ChatCommand.LoadPortionOfMessages(
                streamName = state.streamName,
                topicName = state.topicName,
                anchor = event.anchor
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
        val itemsInState = state.items.toMutableList()
        val itemForUpdate = itemsInState.find { it.id == event.messageId }
        val emojiForUpdate = itemForUpdate?.emojis?.find {
            fromHexToDecimal(it.code) == event.emojiCode
        }
        if (emojiForUpdate != null) {
            emojiForUpdate.count = emojiForUpdate.count.plus(1)
            emojiForUpdate.selectedByCurrentUser = true
        } else {
            itemForUpdate?.emojis?.add(EmojiWithCount(code = event.emojiCode, count = 1, selectedByCurrentUser = true))
        }
        state {
            copy(
                items = itemsInState,
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
        val itemsInState = state.items.toMutableList()
        val itemForUpdate = itemsInState.find { it.id == event.messageId }
        val emojiForUpdate = itemForUpdate?.emojis?.find {
            fromHexToDecimal(it.code) == event.emojiCode
        }
        if (emojiForUpdate != null && emojiForUpdate.count > 1) {
            emojiForUpdate.count = emojiForUpdate.count.minus(1)
            emojiForUpdate.selectedByCurrentUser = false
        } else {
            itemForUpdate?.emojis?.remove(emojiForUpdate)
        }
        state {
            copy(
                items = itemsInState,
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

    private fun Result.processLoadChatEvent(event: ChatEvent.Ui.LoadChat) {
        state {
            copy(
                isLoading = false,
                error = null
            )
        }
        effects { +ChatEffect.NavigateToChat(event.topicName) }
    }

    private fun Result.processLastMessagesLoadedEvent(
        event: ChatEvent.Internal.LastMessagesLoaded
    ) {
        state {
            copy(
                items = event.items,
                isLoading = false,
                error = null,
                anchor = if (event.items.isNotEmpty()) event.items[0].id - 1 else LAST_MESSAGE_ANCHOR
            )
        }
    }

    private fun Result.processPortionOfMessagesLoadedEvent(
        event: ChatEvent.Internal.PortionOfMessagesLoaded
    ) {
        state {
            copy(
                items = event.items.plus(state.items),
                isLoading = false,
                error = null,
                anchor = if (event.items.isNotEmpty()) event.items[0].id - 1 else state.anchor
            )
        }
    }

    private fun Result.processLoadMessageEvent(
        event: ChatEvent.Internal.MessageLoaded
    ) {
        val itemsInState = state.items.toMutableList()
        val itemForUpdate = itemsInState.find { it.id == event.item.id }
        val indexForUpdate = itemsInState.indexOf(itemForUpdate)
        if (itemForUpdate != null && itemForUpdate != event.item) {
            itemsInState[indexForUpdate] = event.item
        }
        state {
            copy(
                isLoading = false,
                items = itemsInState
            )
        }
    }

    private fun Result.processMessageSentEvent() {
        state {
            copy(error = null)
        }
        commands { +ChatCommand.LoadLastMessages(
            streamName = state.streamName,
            topicName = state.topicName,
            anchor = LAST_MESSAGE_ANCHOR,
            isFirstPosition = true
        ) }
        effects { +ChatEffect.MessageSentEffect }
    }

    private fun Result.processReactionAddedEvent(
        event: ChatEvent.Internal.ReactionAdded
    ) {
        state {
            copy(error = null)
        }
        commands {
            +ChatCommand.LoadMessage(
                messageId = event.messageId
            )
        }
    }

    private fun Result.processReactionRemovedEvent(
        event: ChatEvent.Internal.ReactionRemoved
    ) {
        state {
            copy(error = null)
        }
        commands {
            +ChatCommand.LoadMessage(
                messageId = event.messageId
            )
        }
    }

    private fun Result.processFileUploadedEvent(
        event: ChatEvent.Internal.FileUploaded
    ) {
        state {
            copy(error = null)
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
