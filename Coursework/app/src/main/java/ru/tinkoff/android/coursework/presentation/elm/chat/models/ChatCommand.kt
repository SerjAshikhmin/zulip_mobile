package ru.tinkoff.android.coursework.presentation.elm.chat.models

import okhttp3.MultipartBody

internal sealed class ChatCommand {

    data class LoadMessages(
        val topicName: String,
        val currentAnchor: Long,
        val isFirstPosition: Boolean = false,
        val updateAllMessages: Boolean = false
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

    data class UploadFile(val fileBody: MultipartBody.Part) : ChatCommand()

}
