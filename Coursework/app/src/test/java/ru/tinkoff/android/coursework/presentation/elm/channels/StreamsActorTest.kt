package ru.tinkoff.android.coursework.presentation.elm.channels

import org.junit.Rule
import org.junit.Test
import ru.tinkoff.android.coursework.data.StreamsRepositoryImpl
import ru.tinkoff.android.coursework.domain.channels.ChannelsInteractor
import ru.tinkoff.android.coursework.domain.model.Stream
import ru.tinkoff.android.coursework.domain.model.Topic
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsCommand
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.stub.AppDatabaseStub
import ru.tinkoff.android.coursework.stub.ZulipJsonApiStub
import ru.tinkoff.android.coursework.utils.RxRule

internal class StreamsActorTest {

    @get:Rule
    val rxRule = RxRule()

    @Test
    fun `loadStreamsList by default returns StreamsEvent observable`() {
        val zulipApiStub = ZulipJsonApiStub.instance
        val repository = StreamsRepositoryImpl(zulipApiStub, AppDatabaseStub())
        val interactor = ChannelsInteractor(repository)
        val actor = StreamsActor(interactor)

        val testObserver = actor.execute(
            StreamsCommand.LoadStreamsList(isSubscribedStreams = true)
        ).test()

        testObserver.assertValueAt(
            0,
            StreamsEvent.Internal.StreamsListLoaded(
                listOf(
                    Stream(
                        streamId = 1L,
                        name = "first test stream",
                        topics = listOf(
                            Topic("first test topic")
                        ),
                        isSubscribed = true
                    ),
                    Stream(
                        streamId = 2L,
                        name = "second test stream",
                        topics = listOf(
                            Topic("second test topic"),
                            Topic("third test topic")
                        ),
                        isSubscribed = true
                    )
                )
            )
        )

        testObserver.assertValueAt(
            1,
            StreamsEvent.Internal.StreamsListLoaded(
                listOf(
                    Stream(
                        streamId = 1L,
                        name = "first test stream",
                        topics = listOf(
                            Topic("first test topic")
                        ),
                        isSubscribed = true
                    ),
                    Stream(
                        streamId = 2L,
                        name = "second test stream",
                        topics = listOf(
                            Topic("second test topic"),
                            Topic("third test topic")
                        ),
                        isSubscribed = true
                    ),
                    Stream(
                        streamId = 3L,
                        name = "third test stream",
                        topics = listOf(),
                        isSubscribed = true
                    )
                )
            )
        )
    }

}
