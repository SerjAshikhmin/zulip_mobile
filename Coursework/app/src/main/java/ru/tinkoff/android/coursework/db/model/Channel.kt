package ru.tinkoff.android.coursework.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.tinkoff.android.coursework.api.model.ChannelDto
import ru.tinkoff.android.coursework.api.model.TopicDto

@Entity(tableName = "channel")
internal class Channel (

    @PrimaryKey
    val streamId: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "topics")
    var topics: List<TopicDto>
) {

    fun toChannelDto(): ChannelDto {
        return ChannelDto(
            streamId = streamId,
            name = name,
            topics = topics
        )
    }

}

internal fun List<Channel>.toChannelsDtoList(): List<ChannelDto> = map { channelDto -> channelDto.toChannelDto()}
