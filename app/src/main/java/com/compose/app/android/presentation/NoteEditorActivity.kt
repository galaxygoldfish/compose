package com.compose.app.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.compose.app.android.view.NoteEditorView
import com.compose.app.android.viewmodel.NoteEditorViewModel

class NoteEditorActivity : ComponentActivity() {

    private val viewModel: NoteEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.noteDocumentID.value = intent.extras!!.getString("documentID")
        setContent {
            NoteEditorView(
                viewModel = viewModel,
                context = this
            )
        }
    }

}