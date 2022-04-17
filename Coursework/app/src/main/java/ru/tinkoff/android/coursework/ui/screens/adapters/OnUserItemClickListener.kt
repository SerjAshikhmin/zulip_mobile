package ru.tinkoff.android.coursework.ui.screens.adapters

import ru.tinkoff.android.coursework.api.model.UserDto

internal interface OnUserItemClickListener {

    fun onUserItemClick(user: UserDto)

}
