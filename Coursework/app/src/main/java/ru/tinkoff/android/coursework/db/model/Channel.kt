package ru.tinkoff.android.coursework.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel")
internal class Channel (

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "stream_id")
    val streamId: Long,

    @ColumnInfo(name = "name")
    val name: String
)
