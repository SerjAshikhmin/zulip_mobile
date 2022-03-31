package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.View
import ru.tinkoff.android.coursework.model.Topic

internal interface OnTopicItemClickListener {

    fun onTopicItemClickListener(topicItemView: View?, topic: Topic)

}
