package ru.tinkoff.android.coursework.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import kotlin.math.roundToInt

internal fun Context.dpToPx(dp: Int): Int {
    val displayMetrics: DisplayMetrics = this.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, String>) {
    val intent = Intent(this, T::class.java)
    params.forEach { intent.putExtra(it.first, it.second) }
    startActivity(intent)
}
