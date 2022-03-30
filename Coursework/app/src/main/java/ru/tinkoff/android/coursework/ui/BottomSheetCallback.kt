package ru.tinkoff.android.coursework.ui

import android.view.View

interface BottomSheetCallback {

    fun callbackMethod(selectedView: View?, chosenEmojiCode: String)

}
