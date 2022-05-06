package ru.tinkoff.android.coursework.di.streams

import dagger.Component
import ru.tinkoff.android.coursework.data.StreamsRepository
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.ApplicationComponent
import ru.tinkoff.android.coursework.domain.channels.ChannelsInteractor
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsActor
import ru.tinkoff.android.coursework.presentation.screens.ChannelsFragment
import ru.tinkoff.android.coursework.presentation.screens.StreamsListFragment

@ActivityScope
@Component(
    modules = [StreamsModule::class],
    dependencies = [ApplicationComponent::class]
)
internal interface StreamsComponent {

    fun getStreamsRepository(): StreamsRepository

    fun getChannelsUseCases(): ChannelsInteractor

    fun getStreamsActor(): StreamsActor

    fun inject(streamsListFragment: StreamsListFragment)

    fun inject(channelsFragment: ChannelsFragment)

    @Component.Factory
    interface Factory {

        fun create(
            applicationComponent: ApplicationComponent
        ): StreamsComponent
    }

}
