package ru.tinkoff.android.coursework.di

import android.content.Context
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleActor
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileActor
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileElmStoreFactory

internal class GlobalDi private constructor(
    applicationContext: Context
) {

    private val peopleRepository by lazy { PeopleRepository(applicationContext) }

    private val peopleActor by lazy { PeopleActor(peopleRepository) }

    val peopleElmStoreFactory by lazy { PeopleElmStoreFactory(peopleActor) }

    private val profileActor by lazy { ProfileActor(peopleRepository) }

    val profileElmStoreFactory by lazy { ProfileElmStoreFactory(profileActor) }

    companion object {

        lateinit var INSTANCE: GlobalDi

        fun init(applicationContext: Context) {
            INSTANCE = GlobalDi(applicationContext)
        }
    }

}
