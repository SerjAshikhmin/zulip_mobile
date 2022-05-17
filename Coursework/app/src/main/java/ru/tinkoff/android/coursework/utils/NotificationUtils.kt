package ru.tinkoff.android.coursework.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import retrofit2.HttpException
import ru.tinkoff.android.coursework.R
import java.net.UnknownHostException

internal fun View.showSnackBarWithRetryAction(
    text: CharSequence,
    duration: Int,
    action: () -> Unit
) {
    Snackbar.make(this, text, duration).apply {
        setAction(context.getString(R.string.retry_action_snack_bar_text)) { action() }
    }
        .show()
}

internal fun Context.checkUnknownHostException(error: Throwable): Boolean {
    return if (error is UnknownHostException) {
        Toast.makeText(
            this,
            resources.getString(R.string.connection_error_text),
            Toast.LENGTH_SHORT
        ).show()
        true
    } else {
        false
    }
}

internal fun Context.checkHttpTooManyRequestsException(error: Throwable): Boolean {
    return if (error is HttpException && error.code() == 429) {
        Toast.makeText(
            this,
            resources.getString(R.string.too_many_requests_error_text),
            Toast.LENGTH_SHORT
        ).show()
        true
    } else {
        false
    }
}
