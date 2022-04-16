package ru.tinkoff.android.coursework.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.tinkoff.android.coursework.api.model.StreamDto
import ru.tinkoff.android.coursework.api.model.TopicDto

@Entity(tableName = "stream")
internal class Stream (

    @PrimaryKey
    val streamId: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "topics")
    var topics: List<TopicDto>
) {

    fun toStreamDto(): StreamDto {
        return StreamDto(
            streamId = streamId,
            name = name,
            topics = topics
        )
    }

}

internal fun List<Stream>.toStreamsDtoList(): List<StreamDto> = map { stream -> stream.toStreamDto()}
