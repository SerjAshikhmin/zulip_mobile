package ru.tinkoff.android.coursework.presentation.elm.chat.models

import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.db.model.Message

internal sealed class ChatEvent {

    sealed class Ui : ChatEvent() {

        object InitEvent : ChatEvent.Ui()

        data class LoadMessages(
            val topicName: String,
            val currentAnchor: Long,
            val isFirstPortion: Boolean = false,
            val updateAllMessages: Boolean = false
        ) : ChatEvent.Ui()

        data class SendMessage(
            val topicName: String,
            val streamName: String,
            val content: String
        ) : ChatEvent.Ui()

        data class AddReaction(
            val messageId: Long,
            val emojiName: String
        ) : ChatEvent.Ui()

        data class RemoveReaction(
            val messageId: Long,
            val emojiName: String
        ) : ChatEvent.Ui()

        data class UploadFile(val fileBody: MultipartBody.Part) : ChatEvent.Ui()

    }

    sealed class Internal : ChatEvent() {

        data class MessagesLoaded(
            val items: List<Message>,
            val topicName: String,
            val isFirstPortion: Boolean = false,
            val updateAllMessages: Boolean = false
        ) : Internal()

        object MessageSent : Internal()

        object ReactionAdded : Internal()

        object ReactionRemoved : Internal()

        data class FileUploaded(val uri: String) : Internal()

        data class MessagesLoadingError(val error: Throwable) : Internal()

        data class MessageSendingError(val error: Throwable) : Internal()

        data class ReactionAddingError(val error: Throwable) : Internal()

        data class ReactionRemovingError(val error: Throwable) : Internal()

        data class FileUploadingError(val error: Throwable) :Internal()

    }

}
