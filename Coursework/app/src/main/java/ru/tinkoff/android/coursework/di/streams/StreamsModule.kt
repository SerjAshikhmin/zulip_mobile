package ru.tinkoff.android.coursework.di.streams

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.data.StreamsRepository
import ru.tinkoff.android.coursework.data.StreamsRepositoryImpl
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.domain.channels.ChannelsUseCases
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsActor
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsElmStoreFactory

@Module(includes = [StreamsModule.BindingModule::class])
internal class StreamsModule {

    @Provides
    @ActivityScope
    fun provideChannelsUseCases(repository: StreamsRepository): ChannelsUseCases {
        return ChannelsUseCases(repository)
    }

    @Provides
    @ActivityScope
    fun provideStreamsActor(channelsUseCases: ChannelsUseCases): StreamsActor {
        return StreamsActor(channelsUseCases)
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
