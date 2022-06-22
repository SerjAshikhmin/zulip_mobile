package ru.tinkoff.android.coursework

import android.app.Application
import ru.tinkoff.android.coursework.di.ApplicationComponent
import ru.tinkoff.android.coursework.di.DaggerApplicationComponent
import ru.tinkoff.android.coursework.di.NetworkComponent

class App : Application() {

    internal lateinit var applicationComponent: ApplicationComponent
    internal lateinit var networkComponent: NetworkComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.factory().create(
            applicationContext = this
        )
    }

}
