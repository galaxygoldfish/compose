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
