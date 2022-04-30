package ru.tinkoff.android.coursework.presentation.elm.people.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import ru.tinkoff.android.coursework.data.api.model.UserDto

@Parcelize
internal data class PeopleState(
    val items: @RawValue List<UserDto> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = false
) : Parcelable
