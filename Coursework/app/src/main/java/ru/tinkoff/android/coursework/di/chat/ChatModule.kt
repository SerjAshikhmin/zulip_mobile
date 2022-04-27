package ru.tinkoff.android.coursework.di.chat

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tinkoff.android.coursework.data.ChatRepository
import ru.tinkoff.android.coursework.data.ChatRepositoryImpl
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.domain.chat.ChatUseCases
import ru.tinkoff.android.coursework.presentation.elm.chat.ChatActor
import ru.tinkoff.android.coursework.presentation.elm.chat.ChatElmStoreFactory

@Module(includes = [ChatModule.BindingModule::class])
internal class ChatModule {

    @Provides
    @ActivityScope
    fun provideChatUseCases(repository: ChatRepository): ChatUseCases {
        return ChatUseCases(repository)
    }

    @Provides
    @ActivityScope
    fun provideChatActor(chatUseCases: ChatUseCases): ChatActor {
        return ChatActor(chatUseCases)
    }

    @Provides
    @ActivityScope
    fun provideChatElmStoreFactory(chatActor: ChatActor): ChatElmStoreFactory {
        return ChatElmStoreFactory(chatActor)
    }

    @Module
    interface BindingModule {

        @Binds
        fun bindChatRepositoryImpl(impl: ChatRepositoryImpl): ChatRepository
    }

}
