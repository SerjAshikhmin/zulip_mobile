package ru.tinkoff.android.coursework.data.api.model

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.tinkoff.android.coursework.data.db.model.EmojiWithCountDb

internal class Converters {

    @TypeConverter
    fun fromTopics(topics: List<TopicDto>): String = Json.encodeToString(topics)

    @TypeConverter
    fun toTopics(topicsStr: String): List<TopicDto> = Json.decodeFromString(topicsStr)

    @TypeConverter
    fun fromEmojis(emojis: List<EmojiWithCountDb>): String = Json.encodeToString(emojis)

    @TypeConverter
    fun toEmojis(emojisStr: String): List<EmojiWithCountDb> = Json.decodeFromString(emojisStr)

}
