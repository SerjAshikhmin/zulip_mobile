package ru.tinkoff.android.coursework.presentation.elm.chat.models

import okhttp3.MultipartBody

internal sealed class ChatCommand {

    data class LoadLastMessages(
        val streamName: String,
        val topicName: String,
        val anchor: Long
    ) : ChatCommand()

    data class LoadPortionOfMessages(
        val streamName: String,
        val topicName: String,
        val anchor: Long
    ) : ChatCommand()

    data class LoadMessage(
        val messageId: Long
    ) : ChatCommand()

    data class SendMessage(
        val topicName: String,
        val streamName: String,
        val content: String
    ) : ChatCommand()

    data class AddReaction(
        val messageId: Long,
        val emojiName: String
    ) : ChatCommand()

    data class RemoveReaction(
        val messageId: Long,
        val emojiName: String
    ) : ChatCommand()

    data class UploadFile(
        val fileName: String,
        val fileBody: MultipartBody.Part
    ) : ChatCommand()

    data class DeleteMessage(
        val messageId: Long
    ) : ChatCommand()

    data class EditMessage(
        val messageId: Long,
        val topicName: String,
        val content: String
    ) : ChatCommand()

}
