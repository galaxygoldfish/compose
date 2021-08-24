package com.compose.app.android.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.firebase.FirebaseDocument
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

}