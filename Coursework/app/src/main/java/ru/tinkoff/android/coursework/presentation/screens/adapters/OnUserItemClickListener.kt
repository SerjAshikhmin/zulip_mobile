package ru.tinkoff.android.coursework.presentation.screens.adapters

import ru.tinkoff.android.coursework.domain.model.User

internal interface OnUserItemClickListener {

    fun onUserItemClick(user: User)

}
