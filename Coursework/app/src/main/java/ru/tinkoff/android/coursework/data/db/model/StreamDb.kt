package ru.tinkoff.android.coursework.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.tinkoff.android.coursework.data.api.model.TopicDto

@Entity(tableName = "stream")
internal class StreamDb (

    @PrimaryKey
    val streamId: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "topics")
    var topics: List<TopicDto>,

    @ColumnInfo(name = "is_subscribed")
    var isSubscribed: Boolean = false
)
