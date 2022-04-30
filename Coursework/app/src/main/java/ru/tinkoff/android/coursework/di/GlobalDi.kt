package ru.tinkoff.android.coursework.di

import android.content.Context
import ru.tinkoff.android.coursework.data.ChatRepositoryImpl
import ru.tinkoff.android.coursework.data.StreamsRepositoryImpl
import ru.tinkoff.android.coursework.data.PeopleRepositoryImpl
import ru.tinkoff.android.coursework.domain.channels.ChannelsUseCases
import ru.tinkoff.android.coursework.domain.chat.ChatUseCases
import ru.tinkoff.android.coursework.domain.people.PeopleUseCases
import ru.tinkoff.android.coursework.domain.profile.ProfileUseCases
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsActor
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.chat.ChatActor
import ru.tinkoff.android.coursework.presentation.elm.chat.ChatElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleActor
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileActor
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileElmStoreFactory

internal class GlobalDi private constructor(
    applicationContext: Context
) {

    private val peopleRepository by lazy { PeopleRepositoryImpl(applicationContext) }

    private val peopleUseCases by lazy { PeopleUseCases(peopleRepository) }

    private val peopleActor by lazy { PeopleActor(peopleUseCases) }

    val peopleElmStoreFactory by lazy { PeopleElmStoreFactory(peopleActor) }

    private val profileUseCases by lazy { ProfileUseCases(peopleRepository) }

    private val profileActor by lazy { ProfileActor(profileUseCases) }

    val profileElmStoreFactory by lazy { ProfileElmStoreFactory(profileActor) }

    private val streamsRepository by lazy { StreamsRepositoryImpl(applicationContext) }

    private val channelsUseCases by lazy { ChannelsUseCases(streamsRepository) }

    private val streamsActor by lazy { StreamsActor(channelsUseCases) }

    val streamsElmStoreFactory by lazy { StreamsElmStoreFactory(streamsActor) }

    private val chatRepository by lazy { ChatRepositoryImpl(applicationContext) }

    private val chatUseCases by lazy { ChatUseCases(chatRepository) }

    private val chatActor by lazy { ChatActor(chatUseCases) }

    val chatElmStoreFactory by lazy { ChatElmStoreFactory(chatActor) }

    companion object {

        lateinit var INSTANCE: GlobalDi

        fun init(applicationContext: Context) {
            INSTANCE = GlobalDi(applicationContext)
        }
    }

}
