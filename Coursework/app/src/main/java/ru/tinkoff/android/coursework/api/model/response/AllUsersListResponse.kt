package ru.tinkoff.android.coursework.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.api.model.UserDto

@Serializable
internal class AllUsersListResponse (

    @SerialName("members")
    val members: List<UserDto>
)
