package ru.tinkoff.android.coursework.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.tinkoff.android.coursework.data.db.model.User

internal const val SELF_USER_ID = 491498L

@Serializable
internal data class UserDto (

    @SerialName("user_id")
    val userId: Long,

    @SerialName("full_name")
    val fullName: String? = "",

    @SerialName("email")
    val email: String? = "",

    @SerialName("avatar_url")
    val avatarUrl: String?,

    var presence: String? = "undefined"
) {

    fun toUserDb(): User {
        return User(
            userId = userId,
            fullName = fullName,
            email = email,
            avatarUrl = avatarUrl,
            presence = presence
        )
    }

}
