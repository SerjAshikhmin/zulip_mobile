package ru.tinkoff.android.coursework.presentation.screens.adapters

import ru.tinkoff.android.coursework.data.api.model.UserDto

internal interface OnUserItemClickListener {

    fun onUserItemClick(user: UserDto)

}
