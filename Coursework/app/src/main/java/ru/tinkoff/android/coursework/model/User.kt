package ru.tinkoff.android.coursework.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal const val SELF_USER_ID = 491498L

@Serializable
internal data class User (

    @SerialName("user_id")
    val id: Long,

    @SerialName("full_name")
    val name: String? = "",

    val email: String? = "",

    @SerialName("avatar_url")
    val avatarUrl: String?,

    var presence: String? = "undefined"
)
