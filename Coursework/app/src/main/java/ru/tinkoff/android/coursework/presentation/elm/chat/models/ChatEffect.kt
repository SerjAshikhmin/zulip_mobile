package ru.tinkoff.android.coursework.presentation.elm.chat.models

internal sealed class ChatEffect {

    data class MessagesLoadingError(val error: Throwable) : ChatEffect()

    data class MessageSendingError(val error: Throwable) : ChatEffect()

    data class FileUploadingError(val error: Throwable) : ChatEffect()

}
