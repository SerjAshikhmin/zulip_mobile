package ru.tinkoff.android.coursework.utils

import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

internal val View.measuredWidthWithMargins: Int
    get() = measuredWidth + marginRight + marginLeft

internal val View.measuredHeightWithMargins: Int
    get() = measuredHeight + marginTop + marginBottom
