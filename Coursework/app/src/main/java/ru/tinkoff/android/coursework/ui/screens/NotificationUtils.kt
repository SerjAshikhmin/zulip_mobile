package ru.tinkoff.android.coursework.ui.screens

import android.view.View
import com.google.android.material.snackbar.Snackbar
import ru.tinkoff.android.coursework.R

internal fun showSnackBarWithRetryAction(
    view: View,
    text: CharSequence,
    duration: Int,
    action: () -> Unit
) {
    Snackbar.make(view, text, duration).apply {
        setAction(context.getString(R.string.retry_action_snack_bar_text)) { action() }
    }
        .show()
}
