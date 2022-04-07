package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.View
import ru.tinkoff.android.coursework.model.User

internal interface OnUserItemClickListener {

    fun onTopicItemClickListener(topicItemView: View?, user: User)

}