package ru.tinkoff.android.coursework.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.db.AppDatabase
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetModule::class, DatabaseModule::class]
)
internal interface ApplicationComponent {

    fun getAppDatabase(): AppDatabase

    fun getApplicationContext(): Context

    fun getZulipJsonApi(): ZulipJsonApi

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance
            applicationContext: Context
        ): ApplicationComponent
    }

}
