package ru.tinkoff.android.coursework.di.chat

import dagger.Component
import ru.tinkoff.android.coursework.data.ChatRepository
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.ApplicationComponent
import ru.tinkoff.android.coursework.domain.chat.ChatUseCases
import ru.tinkoff.android.coursework.presentation.elm.chat.ChatActor
import ru.tinkoff.android.coursework.presentation.screens.ChatActivity

@ActivityScope
@Component(
    modules = [ChatModule::class],
    dependencies = [ApplicationComponent::class]
)
internal interface ChatComponent {

    fun getChatRepository(): ChatRepository

    fun getChatUseCases(): ChatUseCases

    fun getChatActor(): ChatActor

    fun inject(chatActivity: ChatActivity)

    @Component.Factory
    interface Factory {

        fun create(
            applicationComponent: ApplicationComponent
        ): ChatComponent
    }

}
