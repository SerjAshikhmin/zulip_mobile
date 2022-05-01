package ru.tinkoff.android.coursework.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
internal class UserDb (

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
)
