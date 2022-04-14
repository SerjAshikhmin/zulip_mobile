package ru.tinkoff.android.coursework.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.tinkoff.android.coursework.api.model.UserDto

@Entity(tableName = "user")
internal class User (

    @PrimaryKey
    val userId: Long = 0,

    @ColumnInfo(name = "user_full_name")
    val fullName: String? = "",

    @ColumnInfo(name = "email")
    val email: String? = "",

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @ColumnInfo(name = "presence")
    var presence: String? = "undefined"
) {

    fun toUserDto(): UserDto {
        return UserDto(
            userId = userId,
            fullName = fullName,
            email = email,
            avatarUrl = avatarUrl,
            presence = presence
        )
    }

}

internal fun List<User>.toUsersDtoList(): List<UserDto> = map { userDto -> userDto.toUserDto()}
