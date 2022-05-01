package ru.tinkoff.android.coursework.di.streams

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.data.StreamsRepository
import ru.tinkoff.android.coursework.data.StreamsRepositoryImpl
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.domain.channels.ChannelsInteractor
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsActor
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsElmStoreFactory

@Module(includes = [StreamsModule.BindingModule::class])
internal class StreamsModule {

    @Provides
    @ActivityScope
    fun provideChannelsUseCases(repository: StreamsRepository): ChannelsInteractor {
        return ChannelsInteractor(repository)
    }

    @Provides
    @ActivityScope
    fun provideStreamsActor(channelsInteractor: ChannelsInteractor): StreamsActor {
        return StreamsActor(channelsInteractor)
    }

    @Provides
    @ActivityScope
    fun provideStreamsElmStoreFactory(streamsActor: StreamsActor): StreamsElmStoreFactory {
        return StreamsElmStoreFactory(streamsActor)
    }

    @Module
    interface BindingModule {

        @Binds
        fun bindStreamsRepositoryImpl(impl: StreamsRepositoryImpl): StreamsRepository
    }

}
