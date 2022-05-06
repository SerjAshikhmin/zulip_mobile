package ru.tinkoff.android.coursework.presentation.screens.adapters

import ru.tinkoff.android.coursework.domain.model.Topic

internal interface OnTopicItemClickListener {

    fun onTopicItemClick(topic: Topic, streamName: String)

}
