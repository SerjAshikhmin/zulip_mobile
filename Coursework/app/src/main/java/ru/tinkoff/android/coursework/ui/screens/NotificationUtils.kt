package ru.tinkoff.android.coursework.ui.screens

import android.view.View
import com.google.android.material.snackbar.Snackbar

internal fun showSnackBarWithRetryAction(
    view: View,
    text: CharSequence,
    duration: Int,
    action: () -> Unit
) {
    val snackbar = Snackbar.make(view, text, duration)
    snackbar.setAction("Retry") {
        action()
    }
    snackbar.show()
}
