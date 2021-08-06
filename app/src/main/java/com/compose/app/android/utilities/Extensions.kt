package com.compose.app.android.utilities

import android.content.Context
import androidx.annotation.StringRes

fun Context.rawStringResource(@StringRes id: Int) : String {
    return resources.getString(id)
}