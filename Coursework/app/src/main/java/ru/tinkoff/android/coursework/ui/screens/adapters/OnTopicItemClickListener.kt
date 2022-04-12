package ru.tinkoff.android.coursework.ui.screens.adapters

import ru.tinkoff.android.coursework.api.model.Topic

internal interface OnTopicItemClickListener {

    fun onTopicItemClick(topic: Topic, channelName: String)

}
