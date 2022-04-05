package ru.tinkoff.android.coursework.model.response

import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.model.User

@Serializable
internal data class AllUsersListResponse (
    val members: List<User>
)
