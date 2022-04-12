package ru.tinkoff.android.coursework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.tinkoff.android.coursework.db.dao.*
import ru.tinkoff.android.coursework.db.model.*
import ru.tinkoff.android.coursework.db.model.Channel
import ru.tinkoff.android.coursework.db.model.EmojiWithCount
import ru.tinkoff.android.coursework.db.model.Message
import ru.tinkoff.android.coursework.db.model.Reaction
import ru.tinkoff.android.coursework.db.model.User

@Database(entities = [
    User::class,
    Message::class,
    Channel::class,
    Topic::class,
    EmojiWithCount::class,
    Reaction::class
], version = 1)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun messageDao(): MessageDao

    abstract fun channelDao(): ChannelDao

    abstract fun topicDao(): TopicDao

    abstract fun emojiWithCountDao(): EmojiWithCountDao

    abstract fun reactionDao(): ReactionDao

}
