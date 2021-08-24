package com.compose.app.android.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.ExperimentalTextOnlyTextField
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.theme.IconBackArrow
import com.compose.app.android.theme.IconSaveContent
import com.compose.app.android.theme.IconThemeColor
import com.compose.app.android.theme.IconTrashItem
import com.compose.app.android.viewmodel.NoteEditorViewModel

@Composable
fun NoteEditorView(
    viewModel: NoteEditorViewModel,
    context: Context,
    navController: NavController,
    documentID: String
) {
    viewModel.apply {
        noteDocumentID.value = documentID
        updateNoteContents()
    }
    val titleTextValue = remember { viewModel.titleTextValue }
    val contentTextValue = remember { viewModel.contentTextValue }

    ComposeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.surface
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(start = 15.dp, top = 5.dp)
                            .size(30.dp),
                        onClick = {
                            viewModel.saveNoteContents()
                            viewModel.clearTextFields()
                            navController.navigate(NavigationDestination.ProductivityActivity)
                        },
                        content = @Composable {
                            Icon(
                                painter = painterResource(id = IconBackArrow),
                                contentDescription = stringResource(id = R.string.back_button_content_desc)
                            )
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.padding(end = 5.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(30.dp),
                            onClick = {

                            },
                            content = @Composable {
                                Icon(
                                    painter = painterResource(id = IconSaveContent),
                                    contentDescription = stringResource(id = R.string.save_button_content_desc)
                                )
                            }
                        )
                        IconButton(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(30.dp),
                            onClick = {

                            },
                            content = @Composable {
                                Icon(
                                    painter = painterResource(id = IconThemeColor),
                                    contentDescription = stringResource(id = R.string.palette_icon_content_desc)
                                )
                            }
                        )
                        IconButton(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(30.dp),
                            onClick = {
                                FirebaseDocument().deleteDocument(viewModel.noteDocumentID.value!!, DocumentType.NOTE)
                                viewModel.clearTextFields()
                                navController.navigate(NavigationDestination.ProductivityActivity)
                            },
                            content = @Composable {
                                Icon(
                                    painter = painterResource(id = IconTrashItem),
                                    contentDescription = stringResource(id = R.string.delete_icon_content_desc)
                                )
                            }
                        )
                    }
                }
                ExperimentalTextOnlyTextField(
                    textFieldValue = titleTextValue.value,
                    hint = stringResource(id = R.string.note_editor_title_placeholder),
                    textStyle = MaterialTheme.typography.h2,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                    onValueChange = { newValue ->
                        titleTextValue.value = newValue
                    },
                )
                ExperimentalTextOnlyTextField(
                    textFieldValue = contentTextValue.value,
                    hint = stringResource(id = R.string.note_editor_content_placeholder),
                    textStyle = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 20.dp, top = 15.dp),
                    onValueChange = { newValue ->
                        contentTextValue.value = newValue
                    }
                )
            }
        }
    }
}