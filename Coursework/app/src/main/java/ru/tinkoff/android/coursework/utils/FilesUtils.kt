package ru.tinkoff.android.coursework.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.InputStream
import java.io.OutputStream

internal fun getFileNameFromContentUri(context: Context, contentUri: Uri): String {
    val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
    var fileName = ""
    context.contentResolver.query(contentUri, projection, null, null, null)
        ?.use { metaCursor ->
            if (metaCursor.moveToFirst()) {
                fileName = metaCursor.getString(0)
            }
        }
    return fileName
}

internal fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(BUFFER_SIZE)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}

private const val BUFFER_SIZE = 8192
