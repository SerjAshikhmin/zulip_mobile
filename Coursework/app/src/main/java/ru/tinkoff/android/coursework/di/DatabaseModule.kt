package ru.tinkoff.android.coursework.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.data.db.AppDatabase
import javax.inject.Singleton

@Module
internal class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    companion object {

        private const val DATABASE_NAME = "appDB"
    }

}
