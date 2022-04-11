package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.View

internal interface OnBottomSheetChooseEmojiListener {

    fun onBottomSheetChooseEmoji(selectedView: View?, chosenEmojiCode: String)

}
