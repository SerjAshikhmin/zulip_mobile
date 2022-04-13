package ru.tinkoff.android.coursework.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal const val SELF_USER_ID = 491498L

@Serializable
internal data class User (

    @SerialName("user_id")
    val userId: Long,

    @SerialName("full_name")
    val fullName: String? = "",

    @SerialName("email")
    val email: String? = "",

    @SerialName("avatar_url")
    val avatarUrl: String?,

    var presence: String? = "undefined"
)
