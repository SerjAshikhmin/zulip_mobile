package ru.tinkoff.android.coursework

import android.app.Application
import ru.tinkoff.android.coursework.di.ApplicationComponent
import ru.tinkoff.android.coursework.di.DaggerApplicationComponent

class App : Application() {

    internal lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.factory().create(
            applicationContext = this
        )
    }

}
