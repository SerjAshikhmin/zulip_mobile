package ru.tinkoff.android.coursework.data.mappers

import ru.tinkoff.android.coursework.data.api.model.TopicDto
import ru.tinkoff.android.coursework.domain.model.Topic

internal object TopicMapper {

    fun topicsToTopicsDtoList(topics: List<Topic>): List<TopicDto> =
        topics.map { topic -> topicToTopicDto(topic) }

    fun topicsDtoToTopicsList(topicsDto: List<TopicDto>): List<Topic> =
        topicsDto.map { topic -> topicDtoToTopic(topic) }

    private fun topicToTopicDto(topic: Topic): TopicDto {
        return TopicDto(
            name = topic.name
        )
    }

    private fun topicDtoToTopic(topicDto: TopicDto): Topic {
        return Topic(
            name = topicDto.name
        )
    }

}
