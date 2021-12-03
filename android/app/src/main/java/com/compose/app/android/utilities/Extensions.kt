/**
 * Copyright (C) 2021  Sebastian Hriscu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **/
package com.compose.app.android.utilities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager

/**
 * Get a string by it's ID.
 * @return The value of the string resource
 * @param id - The string resource to be retrieved (R.string....)
 */
fun Context.rawStringResource(@StringRes id: Int): String {
    return resources.getString(id)
}

/**
 * Get the SharedPreferences used by the whole app
 * @return The SharedPreferences instance needed
 */
fun Context.getDefaultPreferences(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}

/**
 * Used to size the profile image into a perfect square to
 * address the case when the photo uploaded isn't a perfect
 * square
 */
fun Bitmap.createSquareImage() : Bitmap = Bitmap.createScaledBitmap(this, 100, 100, false)