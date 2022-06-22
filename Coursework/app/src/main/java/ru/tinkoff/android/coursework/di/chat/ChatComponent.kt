package ru.tinkoff.android.coursework.di.chat

import dagger.Component
import ru.tinkoff.android.coursework.domain.interfaces.ChatRepository
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.ApplicationComponent
import ru.tinkoff.android.coursework.di.NetworkComponent
import ru.tinkoff.android.coursework.domain.chat.ChatInteractor
import ru.tinkoff.android.coursework.presentation.elm.chat.ChatActor
import ru.tinkoff.android.coursework.presentation.screens.ChatActivity

@ActivityScope
@Component(
    modules = [ChatModule::class],
    dependencies = [ApplicationComponent::class, NetworkComponent::class]
)
internal interface ChatComponent {

    fun getChatRepository(): ChatRepository

    fun getChatUseCases(): ChatInteractor

    fun getChatActor(): ChatActor

    fun inject(chatActivity: ChatActivity)

    @Component.Factory
    interface Factory {

        fun create(
            applicationComponent: ApplicationComponent,
            networkComponent: NetworkComponent
        ): ChatComponent
    }

}
