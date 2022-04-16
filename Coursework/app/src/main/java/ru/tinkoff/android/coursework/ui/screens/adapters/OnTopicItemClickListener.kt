package ru.tinkoff.android.coursework.ui.screens.adapters

import ru.tinkoff.android.coursework.api.model.TopicDto

internal interface OnTopicItemClickListener {

    fun onTopicItemClick(topic: TopicDto, streamName: String)

}
