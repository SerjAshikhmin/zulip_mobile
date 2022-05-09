package ru.tinkoff.android.coursework.presentation.elm.chat.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import ru.tinkoff.android.coursework.domain.chat.ChatInteractor
import ru.tinkoff.android.coursework.domain.model.Message

@Parcelize
internal data class ChatState(
    val items: @RawValue List<Message> = emptyList(),
    val error: Throwable? = null,
    val streamName: String = "",
    val topicName: String = "",
    val isLoading: Boolean = false,
    val anchor: Long = ChatInteractor.LAST_MESSAGE_ANCHOR
) : Parcelable
