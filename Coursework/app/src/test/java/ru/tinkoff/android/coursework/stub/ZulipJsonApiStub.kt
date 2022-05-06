package ru.tinkoff.android.coursework.stub

import io.reactivex.Single
import org.mockito.Mockito
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi
import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.data.api.model.TopicDto
import ru.tinkoff.android.coursework.data.api.model.response.SubscribedStreamsListResponse
import ru.tinkoff.android.coursework.data.api.model.response.TopicsListResponse

internal object ZulipJsonApiStub {

    var instance: ZulipJsonApi = Mockito.mock(ZulipJsonApi::class.java)

    init {
        mockGetSubscribedStreamsCall()
        mockGetTopicsInStreamCall()
    }

    private fun mockGetSubscribedStreamsCall() {
        Mockito.`when`(instance.getSubscribedStreams()).thenReturn(
            Single.just(
                SubscribedStreamsListResponse(
                    listOf(
                        StreamDto(
                            streamId = 1L,
                            name = "first test stream",
                            topics = listOf()
                        ),
                        StreamDto(
                            streamId = 2L,
                            name = "second test stream",
                            topics = listOf()
                        ),
                        StreamDto(
                            streamId = 3L,
                            name = "third test stream",
                            topics = listOf()
                        )
                    )
                )
            )
        )
    }

    private fun mockGetTopicsInStreamCall() {
        Mockito.`when`(instance.getTopicsInStream(1L)).thenReturn(
            Single.just(
                TopicsListResponse(
                    listOf(
                        TopicDto("first test topic")
                    )
                )
            )
        )

        Mockito.`when`(instance.getTopicsInStream(2L)).thenReturn(
            Single.just(
                TopicsListResponse(
                    listOf(
                        TopicDto("second test topic"),
                        TopicDto("third test topic")
                    )
                )
            )
        )

        Mockito.`when`(instance.getTopicsInStream(3L)).thenReturn(
            Single.just(
                TopicsListResponse(
                    listOf()
                )
            )
        )
    }

}
