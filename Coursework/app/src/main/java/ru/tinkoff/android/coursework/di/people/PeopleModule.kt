package ru.tinkoff.android.coursework.di.people

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.domain.interfaces.PeopleRepository
import ru.tinkoff.android.coursework.data.PeopleRepositoryImpl
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.domain.people.PeopleInteractor
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleActor
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleElmStoreFactory

@Module(includes = [PeopleModule.BindingModule::class])
internal class PeopleModule {

    @Provides
    @ActivityScope
    fun providePeopleInteractor(repository: PeopleRepository): PeopleInteractor {
        return PeopleInteractor(repository)
    }

    @Provides
    @ActivityScope
    fun providePeopleActor(peopleInteractor: PeopleInteractor): PeopleActor {
        return PeopleActor(peopleInteractor)
    }

    @Provides
    @ActivityScope
    fun providePeopleElmStoreFactory(peopleActor: PeopleActor): PeopleElmStoreFactory {
        return PeopleElmStoreFactory(peopleActor)
    }

    @Module
    interface BindingModule {

        @Binds
        fun bindPeopleRepositoryImpl(impl: PeopleRepositoryImpl): PeopleRepository
    }

}
