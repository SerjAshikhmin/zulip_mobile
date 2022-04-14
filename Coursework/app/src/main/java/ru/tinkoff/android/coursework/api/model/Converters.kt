package ru.tinkoff.android.coursework.api.model

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class Converters {

    @TypeConverter
    fun fromTopics(topics: List<TopicDto>): String = Json.encodeToString(topics)

    @TypeConverter
    fun toTopics(topicsStr: String): List<TopicDto> = Json.decodeFromString(topicsStr)

    @TypeConverter
    fun fromEmojis(emojis: List<EmojiWithCountDto>): String = Json.encodeToString(emojis)

    @TypeConverter
    fun toEmojis(emojisStr: String): List<EmojiWithCountDto> = Json.decodeFromString(emojisStr)

}
