package ru.tinkoff.android.coursework.di.profile

import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.people.PeopleModule
import ru.tinkoff.android.coursework.domain.profile.ProfileUseCases
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileActor
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileElmStoreFactory

@Module(includes = [PeopleModule.BindingModule::class])
internal class ProfileModule {

    @Provides
    @ActivityScope
    fun provideProfileUseCases(repository: PeopleRepository): ProfileUseCases {
        return ProfileUseCases(repository)
    }

    @Provides
    @ActivityScope
    fun provideProfileActor(profileUseCases: ProfileUseCases): ProfileActor {
        return ProfileActor(profileUseCases)
    }

    @Provides
    @ActivityScope
    fun provideProfileElmStoreFactory(profileActor: ProfileActor): ProfileElmStoreFactory {
        return ProfileElmStoreFactory(profileActor)
    }

}
