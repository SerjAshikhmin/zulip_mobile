package ru.tinkoff.android.coursework.ui.screens.adapters

internal interface OnEmojiClickListener {

    fun onEmojiClick(isSelected: Boolean, emojiCode: String, messageId: Long)

}
