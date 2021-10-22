package com.compose.app.android.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.firebase.FirebaseDocument
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

    fun updateNoteContents() {
        asynchronousScope.launch {
            noteDocumentID.value?.let {
                FirebaseDocument().getDocumentByID(it, DocumentType.NOTE).let { noteData ->
                    titleTextValue.value = TextFieldValue(noteData["title"] as String)
                    contentTextValue.value = TextFieldValue(noteData["content"] as String)
                    synchronousScope.launch {
                        selectedNoteColorCentral.value = (noteData["color"] as Long).toInt()
                        selectedNoteColorRes.value = NoteColorResourceIDs[
                            NoteColorUniversalIDs.indexOf((noteData["color"] as Long).toInt())
                        ]
                    }
                }
            }
        }
    }

    fun saveNoteContents() {
        if (titleTextValue.value.text.isNotEmpty() || contentTextValue.value.text.isNotEmpty()) {
            val noteDocumentMap = mapOf<String, Any>(
                "ID" to noteDocumentID.value!!,
                "title" to titleTextValue.value.text,
                "content" to contentTextValue.value.text,
                "color" to selectedNoteColorCentral.value!!,
                "date" to getCurrentDate(),
                "time" to getCurrentTime()
            )
            FirebaseDocument().saveDocument(
                noteDocumentMap,
                noteDocumentID.value!!,
                DocumentType.NOTE
            )
        }
    }

    fun clearTextFields() {
        contentTextValue = mutableStateOf(TextFieldValue(""))
        titleTextValue = mutableStateOf(TextFieldValue(""))
    }

    fun getCurrentDate() : String {
        val calendar = Calendar.getInstance()
        return """${calendar[Calendar.MONTH] + 1}/${calendar[Calendar.DATE]}"""
    }

    fun getCurrentTime() : String {
        val calendar = Calendar.getInstance()
        val calendarMinute = calendar[Calendar.MINUTE]
        val editedMinute = if (calendarMinute.toString().length == 1) "0$calendarMinute" else calendarMinute
        return """${calendar[Calendar.HOUR]}:${editedMinute}"""
    }
}