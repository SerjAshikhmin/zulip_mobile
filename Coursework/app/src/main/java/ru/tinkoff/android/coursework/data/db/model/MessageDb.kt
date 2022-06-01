package ru.tinkoff.android.coursework.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
internal data class MessageDb (

    @PrimaryKey
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "user_full_name")
    val userFullName: String,

    @ColumnInfo(name = "stream_id")
    val streamId: Long = 0,

    @ColumnInfo(name = "topic_name")
    val topicName: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "emojis")
    val emojis: List<EmojiWithCountDb>,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)
