package ru.tinkoff.android.coursework.utils

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

internal fun Context.dpToPx(dp: Int): Int {
    val displayMetrics: DisplayMetrics = this.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}
