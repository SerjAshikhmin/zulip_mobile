package ru.tinkoff.android.coursework.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat

internal fun getFormattedContentFromHtml(source: String): Spanned? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(source)
    }
}
