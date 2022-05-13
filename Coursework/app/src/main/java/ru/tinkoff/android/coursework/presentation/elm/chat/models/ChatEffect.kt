package ru.tinkoff.android.coursework.presentation.elm.chat.models

import okhttp3.MultipartBody
import ru.tinkoff.android.coursework.domain.model.Message

internal sealed class ChatEffect {

    object MessageSentEffect : ChatEffect()

    data class FileUploadedEffect(
        val fileName: String,
        val fileUri: String
    ) : ChatEffect()

    data class StartEditMessageEffect(
        val message: Message
    ) : ChatEffect()

    object MessageEditedEffect : ChatEffect()

    data class NavigateToChat(val topicName: String) : ChatEffect()

    data class MessagesLoadingError(val error: Throwable) : ChatEffect()

    data class MessageSendingError(val error: Throwable) : ChatEffect()

    data class MessageDeletingError(val error: Throwable) : ChatEffect()

    data class MessageEditingError(val error: Throwable) : ChatEffect()

    data class FileUploadingError(
        val error: Throwable,
        val fileName: String,
        val fileBody: MultipartBody.Part,
    ) : ChatEffect()

}
