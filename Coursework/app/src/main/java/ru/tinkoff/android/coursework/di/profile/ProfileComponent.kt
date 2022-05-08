package ru.tinkoff.android.coursework.di.profile

import dagger.Component
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.ApplicationComponent
import ru.tinkoff.android.coursework.domain.profile.ProfileInteractor
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileActor
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment

@ActivityScope
@Component(
    modules = [ProfileModule::class],
    dependencies = [ApplicationComponent::class]
)
internal interface ProfileComponent {

    fun getProfileInteractor(): ProfileInteractor

    fun getProfileActor(): ProfileActor

    fun inject(profileFragment: ProfileFragment)

    @Component.Factory
    interface Factory {

        fun create(
            applicationComponent: ApplicationComponent
        ): ProfileComponent
    }
}
