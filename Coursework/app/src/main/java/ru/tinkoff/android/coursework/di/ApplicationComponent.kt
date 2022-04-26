package ru.tinkoff.android.coursework.di

import dagger.Component
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.domain.people.PeopleUseCases
import ru.tinkoff.android.coursework.domain.profile.ProfileUseCases
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleActor
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileActor
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileElmStoreFactory
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, PeopleModule::class, ProfileModule::class])
internal interface ApplicationComponent {

    fun getAppDatabase(): AppDatabase?

    fun getPeopleRepository(): PeopleRepository

    fun getPeopleUseCases(): PeopleUseCases

    fun getPeopleActor(): PeopleActor

    fun getProfileUseCases(): ProfileUseCases

    fun getProfileActor(): ProfileActor

    fun getPeopleElmStoreFactory(): PeopleElmStoreFactory

    fun getProfileElmStoreFactory(): ProfileElmStoreFactory

}
