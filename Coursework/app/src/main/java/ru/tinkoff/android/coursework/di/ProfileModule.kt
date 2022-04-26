package ru.tinkoff.android.coursework.di

import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.domain.profile.ProfileUseCases
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileActor
import javax.inject.Singleton

@Module
internal object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileUseCases(repository: PeopleRepository): ProfileUseCases {
        return ProfileUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideProfileActor(profileUseCases: ProfileUseCases): ProfileActor {
        return ProfileActor(profileUseCases)
    }

}
