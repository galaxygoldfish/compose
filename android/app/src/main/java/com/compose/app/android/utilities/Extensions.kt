package com.compose.app.android.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager

/**
 * Get a string by it's ID.
 * @return The value of the string resource
 * @param id - The string resource to be retrieved (R.string....)
 */
fun Context.rawStringResource(@StringRes id: Int) : String {
    return resources.getString(id)
}

/**
 * Get the SharedPreferences used by the whole app
 * @return The SharedPreferences instance needed
 */
fun Context.getDefaultPreferences() : SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}