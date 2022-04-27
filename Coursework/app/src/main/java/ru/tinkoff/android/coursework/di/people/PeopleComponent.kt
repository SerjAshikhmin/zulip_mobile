package ru.tinkoff.android.coursework.di.people

import dagger.Component
import ru.tinkoff.android.coursework.data.PeopleRepository
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.ApplicationComponent
import ru.tinkoff.android.coursework.domain.people.PeopleUseCases
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleActor
import ru.tinkoff.android.coursework.presentation.screens.PeopleFragment

@ActivityScope
@Component(
    modules = [PeopleModule::class],
    dependencies = [ApplicationComponent::class]
)
internal interface PeopleComponent {

    fun getPeopleRepository(): PeopleRepository

    fun getPeopleUseCases(): PeopleUseCases

    fun getPeopleActor(): PeopleActor

    //fun getPeopleElmStoreFactory(): PeopleElmStoreFactory
    fun inject(peopleFragment: PeopleFragment)

    @Component.Factory
    interface Factory {

        fun create(
            applicationComponent: ApplicationComponent
        ): PeopleComponent
    }

}
