package ru.tinkoff.android.coursework.presentation.elm.channels.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import ru.tinkoff.android.coursework.domain.model.Stream

@Parcelize
internal data class StreamsState(
    val items: @RawValue List<Stream> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = false
) : Parcelable
