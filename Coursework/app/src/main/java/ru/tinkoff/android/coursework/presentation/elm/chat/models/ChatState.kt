package ru.tinkoff.android.coursework.presentation.elm.chat.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import ru.tinkoff.android.coursework.domain.model.Message

@Parcelize
internal data class ChatState(
    val items: @RawValue List<Message> = emptyList(),
    val error: Throwable? = null,
    val fileUri: String? = null,
    val isLoading: Boolean = false,
    val updateAllMessages: Boolean = false,
    val updateWithPortion: Boolean = false,
    val isFirstPortion: Boolean = false,
    val isReactionAdded: Boolean = false,
    val isReactionRemoved: Boolean = false,
    val isFileUploaded: Boolean = false,
    val topicName: String = ""
) : Parcelable
