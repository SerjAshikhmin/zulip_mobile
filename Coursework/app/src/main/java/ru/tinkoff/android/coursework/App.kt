package ru.tinkoff.android.coursework

import android.app.Application
import ru.tinkoff.android.coursework.di.ApplicationModule
import ru.tinkoff.android.coursework.di.DaggerApplicationComponent
import ru.tinkoff.android.coursework.di.GlobalDi

internal class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
        GlobalDi.init(applicationContext)
    }

}
