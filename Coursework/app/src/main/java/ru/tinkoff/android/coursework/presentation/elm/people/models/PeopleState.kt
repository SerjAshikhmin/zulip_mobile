package ru.tinkoff.android.coursework.presentation.elm.people.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
internal data class PeopleState(
    val items: @RawValue List<Any> = emptyList(),
    val isEmptyState: Boolean = false,
    val error: Throwable? = null,
    val isLoading: Boolean = false
) : Parcelable
