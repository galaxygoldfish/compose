package com.compose.app.android.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.CardColorBlueAlt
import com.compose.app.android.model.DocumentType
import com.compose.app.android.model.NoteColorResourceIDs
import com.compose.app.android.model.NoteColorUniversalIDs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class NoteEditorViewModel : ViewModel() {

    val noteDocumentID: MutableLiveData<String> = MutableLiveData("")
    val previousDocumentID: MutableLiveData<String> = MutableLiveData("")
    val selectedNoteColorRes: MutableLiveData<Int> = MutableLiveData(NoteColorResourceIDs[7])
    val selectedNoteColorCentral: MutableLiveData<Int> = MutableLiveData(NoteColorUniversalIDs[7])

    var titleTextValue = mutableStateOf(TextFieldValue())
    var contentTextValue = mutableStateOf(TextFieldValue())

    private val asynchronousScope = CoroutineScope(Dispatchers.IO + Job())
    private val synchronousScope = CoroutineScope(Dispatchers.Main + Job())

    /**
     * Fetch the current note's contents from the cloud or clear
     * all text fields and customization values to leave the editor
     * blank.
     */
    fun updateNoteContents() {
        asynchronousScope.launch {
            noteDocumentID.value?.let {
                FirebaseDocument().getDocumentByID(it, DocumentType.NOTE).let { noteData ->
                    titleTextValue.value = TextFieldValue(noteData["TITLE"] as String? ?: "")
                    contentTextValue.value = TextFieldValue(noteData["CONTENT"] as String? ?: "")
                    synchronousScope.launch {
                        selectedNoteColorCentral.value =
                            (noteData["COLOR"] as Long? ?: CardColorBlueAlt).toInt()
                        selectedNoteColorRes.value = NoteColorResourceIDs[
                                NoteColorUniversalIDs.indexOf(
                                    (noteData["COLOR"] as Long? ?: CardColorBlueAlt).toInt()
                                )
                        ]
                    }
                }
            }
        }
    }

    /**
     * Upload note contents to firebase only if there is note content
     * to save.
     */
    fun saveNoteContents() {
        if (titleTextValue.value.text.isNotEmpty() || contentTextValue.value.text.isNotEmpty()) {
            val noteDocumentMap = mapOf<String, Any>(
                "ID" to noteDocumentID.value!!,
                "TITLE" to titleTextValue.value.text,
                "CONTENT" to contentTextValue.value.text,
                "COLOR" to selectedNoteColorCentral.value!!,
                "DATE" to getCurrentDate(),
                "TIME" to getCurrentTime()
            )
            asynchronousScope.launch {
                FirebaseDocument().saveDocument(
                    documentFields = noteDocumentMap,
                    documentID = noteDocumentID.value!!,
                    type = DocumentType.NOTE
                )
            }
        }
    }

    /**
     * Set all editor text fields to be blank
     */
    fun clearTextFields() {
        contentTextValue = mutableStateOf(TextFieldValue(""))
        titleTextValue = mutableStateOf(TextFieldValue(""))
    }

    /**
     * Return a formatted string of the current date
     */
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return """${calendar[Calendar.MONTH] + 1}/${calendar[Calendar.DATE]}"""
    }

    /**
     * Return a parsed string of the current time
     */
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val calendarMinute = calendar[Calendar.MINUTE]
        val editedMinute =
            if (calendarMinute.toString().length == 1) "0$calendarMinute" else calendarMinute
        return """${calendar[Calendar.HOUR]}:${editedMinute}"""
    }
}