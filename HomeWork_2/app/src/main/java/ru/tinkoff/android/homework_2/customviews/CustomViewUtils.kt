package ru.tinkoff.android.homework_2.customviews

import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

val View.measuredWidthWithMargins: Int
    get() {
        return measuredWidth + marginRight + marginLeft
    }

val View.measuredHeightWithMargins: Int
    get() {
        return measuredHeight + marginTop + marginBottom
    }