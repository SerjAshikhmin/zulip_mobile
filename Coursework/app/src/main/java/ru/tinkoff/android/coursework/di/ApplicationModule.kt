package ru.tinkoff.android.coursework.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.data.db.AppDatabase
import javax.inject.Singleton

@Module
internal class ApplicationModule(val application: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    fun providesApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase? {
        return AppDatabase.getAppDatabase(application.applicationContext)
    }

}
