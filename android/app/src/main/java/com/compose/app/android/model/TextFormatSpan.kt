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
package com.compose.app.android.model

import androidx.compose.ui.text.SpanStyle

/**
 * Portable span model class, used to store changes
 * made to the formatting of notes in the editor
 */
data class TextFormatSpan(
    var spanStart: Int,
    var spanEnd: Int,
    var spanType: SpanStyle
)

/**
 * Constants used to decode the values saved to firebase
 * in a more readable way for note formatting
 */
object SavedSpanType {

    // Span types
    const val COLOR_SPAN = 0
    const val BOLD_SPAN = 1
    const val ITALIC_SPAN = 2
    const val UNDERLINE_SPAN = 3
    const val SIZE_SPAN = 4
    const val NULL = 600

    // Color values
    const val COLOR_RED = 0
    const val COLOR_ORANGE = 1
    const val COLOR_YELLOW = 2
    const val COLOR_GREEN = 3
    const val COLOR_BLUE = 4
    const val COLOR_PURPLE = 5

    // Size values are saved as their respective
    // sp value as an int in firebase (STYLE-EXTRA), so they can
    // be applied directly

}