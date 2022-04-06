package ru.tinkoff.android.coursework.ui.screens.adapters

internal interface OnBottomSheetChooseEmojiListener {

    fun onBottomSheetChooseEmoji(isSelected: Boolean, emojiCode: String, messageId: Long)
}
