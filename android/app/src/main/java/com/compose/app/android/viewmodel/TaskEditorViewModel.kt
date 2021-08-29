package com.compose.app.android.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class TaskEditorViewModel : ViewModel() {

    val titleTextFieldValue = mutableStateOf(TextFieldValue(""))
    val contentTextFieldValue = mutableStateOf(TextFieldValue(""))

}