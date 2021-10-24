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

    fun updateNoteContents() {
        asynchronousScope.launch {
            noteDocumentID.value?.let {
                FirebaseDocument().getDocumentByID(it, DocumentType.NOTE).let { noteData ->
                    titleTextValue.value = TextFieldValue(noteData["TITLE"] as String? ?: "")
                    contentTextValue.value = TextFieldValue(noteData["CONTENT"] as String? ?: "")
                    synchronousScope.launch {
                        selectedNoteColorCentral.value = (noteData["COLOR"] as Long? ?: CardColorBlueAlt).toInt()
                        selectedNoteColorRes.value = NoteColorResourceIDs[
                            NoteColorUniversalIDs.indexOf((noteData["COLOR"] as Long? ?: CardColorBlueAlt).toInt())
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
                "TITLE" to titleTextValue.value.text,
                "CONTENT" to contentTextValue.value.text,
                "COLOR" to selectedNoteColorCentral.value!!,
                "DATE" to getCurrentDate(),
                "TIME" to getCurrentTime()
            )
            asynchronousScope.launch {
                FirebaseDocument().saveDocument(
                    noteDocumentMap,
                    noteDocumentID.value!!,
                    DocumentType.NOTE
                )
            }
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