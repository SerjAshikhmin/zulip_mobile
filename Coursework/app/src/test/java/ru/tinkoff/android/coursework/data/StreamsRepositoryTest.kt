package ru.tinkoff.android.coursework.data

import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.domain.model.Stream
import ru.tinkoff.android.coursework.domain.model.Topic
import ru.tinkoff.android.coursework.stub.AppDatabaseStub
import ru.tinkoff.android.coursework.utils.RxRule

internal class StreamsRepositoryTest {

    @get:Rule
    val rxRule = RxRule()

    @Test
    fun `loadStreamsFromDb by default returns streams list single`() {
        val repository = StreamsRepositoryImpl(
            Mockito.mock(ZulipJsonApi::class.java),
            AppDatabaseStub()
        )

        val testObserver = repository.loadStreamsFromDb().test()

        testObserver.assertValue(
            listOf(
                Stream(
                    streamId = 1L,
                    name = "first test stream",
                    topics = listOf(
                        Topic("first test topic")
                    )
                ),
                Stream(
                    streamId = 2L,
                    name = "second test stream",
                    topics = listOf(
                        Topic("second test topic"),
                        Topic("third test topic")
                    )
                )
            )
        )
    }

}
