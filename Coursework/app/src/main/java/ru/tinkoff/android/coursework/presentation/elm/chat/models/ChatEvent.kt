package ru.tinkoff.android.coursework.presentation.elm.chat.models

import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.domain.model.Message

internal sealed class ChatEvent {

    sealed class Ui : ChatEvent() {

        object InitEvent : ChatEvent.Ui()

        data class LoadLastMessages(
            val streamName: String,
            val topicName: String,
            val anchor: Long
        ) : ChatEvent.Ui()

        data class LoadPortionOfMessages(
            val anchor: Long
        ) : ChatEvent.Ui()

        data class SendMessage(
            val topicName: String,
            val streamName: String,
            val content: String
        ) : ChatEvent.Ui()

        data class AddReaction(
            val messageId: Long,
            val emojiName: String,
            val emojiCode: String
        ) : ChatEvent.Ui()

        data class RemoveReaction(
            val messageId: Long,
            val emojiName: String,
            val emojiCode: String
        ) : ChatEvent.Ui()

        data class UploadFile(
            val fileName: String,
            val fileBody: MultipartBody.Part
        ) : ChatEvent.Ui()

        data class LoadChat(
            val topicName: String
        ) : Ui()

        data class DeleteMessage(
            val messageId: Long
        ) : Ui()

        data class StartEditMessage(
            val message: Message
        ) : Ui()

        data class EditMessage(
            val messageId: Long,
            val topicName: String,
            val content: String
        ) : Ui()

    }

    sealed class Internal : ChatEvent() {

        data class LastMessagesLoaded(
            val items: List<Message>,
            val topicName: String
        ) : Internal()

        data class PortionOfMessagesLoaded(
            val items: List<Message>,
            val topicName: String
        ) : Internal()

        data class MessageLoaded(
            val item: Message
        ) : Internal()

        object MessageSent : Internal()

        data class ReactionAdded(
            val messageId: Long
        ) : Internal()

        data class ReactionRemoved(
            val messageId: Long
        ) : Internal()

        data class FileUploaded(
            val fileName: String,
            val fileUri: String
        ) : Internal()

        data class MessageDeleted(
            val messageId: Long
        ) : Internal()

        data class MessageEdited(
            val messageId: Long
        ) : Internal()

        data class MessagesLoadingError(
            val error: Throwable
        ) : Internal()

        data class MessageSendingError(
            val error: Throwable
        ) : Internal()

        data class MessageDeletingError(
            val error: Throwable
        ) : Internal()

        data class MessageEditingError(
            val error: Throwable
        ) : Internal()

        data class FileUploadingError(
            val error: Throwable,
            val fileName: String,
            val fileBody: MultipartBody.Part
        ) :Internal()

    }

}
