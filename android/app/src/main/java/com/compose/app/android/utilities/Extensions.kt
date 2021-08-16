package com.compose.app.android.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager

fun Context.rawStringResource(@StringRes id: Int) : String {
    return resources.getString(id)
}

fun Context.getDefaultPreferences() : SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}