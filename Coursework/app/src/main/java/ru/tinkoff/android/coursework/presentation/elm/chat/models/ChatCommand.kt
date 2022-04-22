package ru.tinkoff.android.coursework.presentation.elm.chat.models

import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.data.db.model.Message

internal sealed class ChatCommand {

    data class LoadMessagesFromApi(val topicName: String, val adapterAnchor: Long) : ChatCommand()

    data class LoadMessagesFromDb(val topicName: String, val adapterAnchor: Long) : ChatCommand()

    data class CacheMessages(val topicName: String, val newMessages: List<Message>, val adapterMessages: List<Message>) : ChatCommand()

    data class SendMessage(val topicName: String, val streamName: String, val content: String) : ChatCommand()

    data class AddReaction(val messageId: Long, val emojiName: String) : ChatCommand()

    data class RemoveReaction(val messageId: Long, val emojiName: String) : ChatCommand()

    data class UploadFile(val fileBody: MultipartBody.Part) : ChatCommand()

}
