package ru.tinkoff.android.coursework

import android.app.Application
import ru.tinkoff.android.coursework.di.GlobalDi

internal class App : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalDi.init(applicationContext)
    }
}
