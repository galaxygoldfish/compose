package com.compose.app.android.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import java.util.Calendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NoteEditorViewModel : ViewModel() {

    val noteDocumentID: MutableLiveData<String> = MutableLiveData("")

    var titleTextValue = mutableStateOf(TextFieldValue())
    var contentTextValue = mutableStateOf(TextFieldValue())

    private val asynchronousScope = CoroutineScope(Dispatchers.IO + Job())

    fun updateNoteContents() {
        asynchronousScope.launch {
            noteDocumentID.value?.let {
                FirebaseDocument().getNoteByID(it).let { noteData ->
                    titleTextValue.value = TextFieldValue(noteData["title"] as String)
                    contentTextValue.value = TextFieldValue(noteData["content"] as String)
                }
            }
        }
    }

    fun saveNoteContents() {
        val calendar = Calendar.getInstance()
        val calendarMinute = calendar[Calendar.MINUTE]
        val editedMinute = if (calendarMinute.toString().length == 1) "0$calendarMinute" else calendarMinute
        val noteDocumentMap = mapOf<String, Any>(
            "ID" to noteDocumentID.value!!,
            "title" to titleTextValue.value.text,
            "content" to contentTextValue.value.text,
            "color" to R.color.note_card_red_alt,
            "date" to """${calendar[Calendar.MONTH] + 1}/${calendar[Calendar.DATE]}""",
            "time" to """${calendar[Calendar.HOUR]}:${editedMinute}"""
        )
        FirebaseDocument().saveDocument(noteDocumentMap, noteDocumentID.value!!, DocumentType.NOTE)
    }

    fun clearTextFields() {
        contentTextValue = mutableStateOf(TextFieldValue(""))
        titleTextValue = mutableStateOf(TextFieldValue(""))
    }
}