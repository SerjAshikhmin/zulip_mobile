package ru.tinkoff.android.coursework.data.mappers

import ru.tinkoff.android.coursework.data.api.model.StreamDto
import ru.tinkoff.android.coursework.data.db.model.StreamDb
import ru.tinkoff.android.coursework.domain.model.Stream

internal object StreamMapper {

    fun streamsToStreamsDbList(streams: List<Stream>): List<StreamDb> =
        streams.map { stream -> streamToStreamDb(stream) }

    fun streamsDbToStreamsList(streamsDb: List<StreamDb>): List<Stream> =
        streamsDb.map { stream -> streamDbToStream(stream) }

    fun streamDtoToStream(streamDto: StreamDto): Stream {
        return Stream(
            streamId = streamDto.streamId,
            name = streamDto.name,
            topics = TopicMapper.topicsDtoToTopicsList(streamDto.topics),
            isSubscribed = streamDto.isSubscribed
        )
    }

    private fun streamToStreamDb(stream: Stream): StreamDb {
        return StreamDb(
            streamId = stream.streamId,
            name = stream.name,
            topics = TopicMapper.topicsToTopicsDtoList(stream.topics),
            isSubscribed = stream.isSubscribed
        )
    }

    private fun streamDbToStream(streamDb: StreamDb): Stream {
        return Stream(
            streamId = streamDb.streamId,
            name = streamDb.name,
            topics = TopicMapper.topicsDtoToTopicsList(streamDb.topics),
            isSubscribed = streamDb.isSubscribed
        )
    }

}
