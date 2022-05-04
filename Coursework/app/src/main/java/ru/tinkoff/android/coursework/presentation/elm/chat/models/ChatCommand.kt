package ru.tinkoff.android.coursework.presentation.elm.chat.models

import okhttp3.MultipartBody

internal sealed class ChatCommand {

    data class LoadLastMessages(
        val topicName: String,
        val currentAnchor: Long,
        val isFirstPosition: Boolean = false
    ) : ChatCommand()

    data class LoadPortionOfMessages(
        val topicName: String,
        val currentAnchor: Long,
        val isFirstPosition: Boolean = false
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

}
