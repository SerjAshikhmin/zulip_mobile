package ru.tinkoff.android.coursework.di.people

import dagger.Component
import ru.tinkoff.android.coursework.domain.interfaces.PeopleRepository
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.ApplicationComponent
import ru.tinkoff.android.coursework.di.NetworkComponent
import ru.tinkoff.android.coursework.domain.people.PeopleInteractor
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleActor
import ru.tinkoff.android.coursework.presentation.screens.PeopleFragment

@ActivityScope
@Component(
    modules = [PeopleModule::class],
    dependencies = [ApplicationComponent::class, NetworkComponent::class]
)
internal interface PeopleComponent {

    fun getPeopleRepository(): PeopleRepository

    fun getPeopleUseCases(): PeopleInteractor

    fun getPeopleActor(): PeopleActor

    fun inject(peopleFragment: PeopleFragment)

    @Component.Factory
    interface Factory {

        fun create(
            applicationComponent: ApplicationComponent,
            networkComponent: NetworkComponent
        ): PeopleComponent
    }

}
