package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.View
import ru.tinkoff.android.coursework.model.Topic

internal interface OnTopicItemClickListener {

    fun onTopicItemClick(topicItemView: View?, topic: Topic, channelName: String)

}
