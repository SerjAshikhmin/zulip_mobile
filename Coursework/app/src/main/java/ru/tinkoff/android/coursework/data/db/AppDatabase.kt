package ru.tinkoff.android.coursework.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.tinkoff.android.coursework.data.api.model.Converters
import ru.tinkoff.android.coursework.data.db.dao.*
import ru.tinkoff.android.coursework.data.db.model.StreamDb
import ru.tinkoff.android.coursework.data.db.model.MessageDb
import ru.tinkoff.android.coursework.data.db.model.UserDb

@Database(entities = [
    UserDb::class,
    MessageDb::class,
    StreamDb::class
], version = 1)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun messageDao(): MessageDao

    abstract fun streamDao(): StreamDao

}
