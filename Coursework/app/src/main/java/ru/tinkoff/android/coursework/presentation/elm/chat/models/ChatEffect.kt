package ru.tinkoff.android.coursework.presentation.elm.chat.models

import okhttp3.MultipartBody

internal sealed class ChatEffect {

    object MessageSentEffect : ChatEffect()

    data class FileUploadedEffect(
        val fileName: String,
        val fileUri: String
    ) : ChatEffect()

    data class NavigateToChat(val topicName: String) : ChatEffect()

    data class MessagesLoadingError(val error: Throwable) : ChatEffect()

    data class MessageSendingError(val error: Throwable) : ChatEffect()

    data class MessageDeletingError(val error: Throwable) : ChatEffect()

    data class FileUploadingError(
        val error: Throwable,
        val fileName: String,
        val fileBody: MultipartBody.Part,
    ) : ChatEffect()

}
