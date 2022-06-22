package ru.tinkoff.android.coursework.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tinkoff.android.coursework.data.db.AppDatabase
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DatabaseModule::class]
)
internal interface ApplicationComponent {

    fun getAppDatabase(): AppDatabase

    fun getApplicationContext(): Context

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance
            applicationContext: Context
        ): ApplicationComponent
    }

}
