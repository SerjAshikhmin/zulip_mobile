package ru.tinkoff.android.coursework.presentation.elm.people.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import ru.tinkoff.android.coursework.domain.model.User

@Parcelize
internal data class PeopleState(
    val items: @RawValue List<User> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = false
) : Parcelable
