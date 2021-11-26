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

/**
 * Model representation of the note document in Firebase
 * @param noteID - The name of the document file or the ID
 * @param color - The integer value associated with the card
 * color selected - @see NoteCardColors.kt
 * @param content - The content of the note
 * @param title - The title of the note
 * @param date - The latest day that the note was edited
 * @param time - The latest time that the note was edited
 */
data class NoteDocument(
    var noteID: String,
    var color: Int,
    var content: String,
    var title: String,
    var date: String,
    var time: String,
)
