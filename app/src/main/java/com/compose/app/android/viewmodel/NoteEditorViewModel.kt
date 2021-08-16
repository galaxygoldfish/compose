package com.compose.app.android.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoteEditorViewModel : ViewModel() {

    val noteDocumentID: MutableLiveData<String> = MutableLiveData("")
    val titleTextValue = TextFieldValue()
    val contentTextValue = TextFieldValue()

}