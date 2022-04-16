package ru.tinkoff.android.coursework.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.tinkoff.android.coursework.api.model.Converters
import ru.tinkoff.android.coursework.db.dao.*
import ru.tinkoff.android.coursework.db.model.Stream
import ru.tinkoff.android.coursework.db.model.Message
import ru.tinkoff.android.coursework.db.model.User

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

    companion object {

        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "appDB"

        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).build()
                }
            }
            return INSTANCE
        }
    }

}
