package ru.tinkoff.android.coursework.di

import android.content.Context
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleActor
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleElmStoreFactory

internal class GlobalDi private constructor(
    applicationContext: Context
) {

    private val peopleRepository by lazy { PeopleRepository(applicationContext) }

    private val peopleActor by lazy { PeopleActor(peopleRepository) }

    val peopleElmStoreFactory by lazy { PeopleElmStoreFactory(peopleActor) }

    companion object {

        lateinit var INSTANCE: GlobalDi

        fun init(applicationContext: Context) {
            INSTANCE = GlobalDi(applicationContext)
        }
    }

}
