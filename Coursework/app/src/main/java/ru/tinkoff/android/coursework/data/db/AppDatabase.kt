package ru.tinkoff.android.coursework.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.tinkoff.android.coursework.data.api.model.Converters
import ru.tinkoff.android.coursework.data.db.dao.*
import ru.tinkoff.android.coursework.data.db.model.Stream
import ru.tinkoff.android.coursework.data.db.model.Message
import ru.tinkoff.android.coursework.data.db.model.User

@Database(entities = [
    User::class,
    Message::class,
    Stream::class
], version = 1)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun messageDao(): MessageDao

    abstract fun streamDao(): StreamDao

}
