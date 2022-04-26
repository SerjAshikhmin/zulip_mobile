package ru.tinkoff.android.coursework.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.data.PeopleRepositoryImpl
import ru.tinkoff.android.coursework.domain.people.PeopleUseCases
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleActor
import javax.inject.Singleton

@Module(includes = [PeopleModule.BindingModule::class])
internal object PeopleModule {

    /*@Provides
    fun providePeopleRepository(context: Context): PeopleRepository {
        return PeopleRepositoryImpl(context)
    }*/

    @Provides
    @Singleton
    fun providePeopleUseCases(repository: PeopleRepository): PeopleUseCases {
        return PeopleUseCases(repository)
    }

    @Provides
    @Singleton
    fun providePeopleActor(peopleUseCases: PeopleUseCases): PeopleActor {
        return PeopleActor(peopleUseCases)
    }

    @Module
    interface BindingModule {

        @Binds
        fun bindPeopleRepositoryImpl(impl: PeopleRepositoryImpl): PeopleRepository
    }

}
