package ru.tinkoff.android.coursework.presentation.screens.adapters

import ru.tinkoff.android.coursework.data.api.model.TopicDto

internal interface OnTopicItemClickListener {

    fun onTopicItemClick(topic: TopicDto, streamName: String)

}
