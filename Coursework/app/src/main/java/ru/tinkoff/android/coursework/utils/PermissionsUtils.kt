package ru.tinkoff.android.coursework.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

internal fun hasPermissions(
    context: Context,
    vararg permissions: String
): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

internal fun permissionGranted(
    grantResults: IntArray
) = (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
