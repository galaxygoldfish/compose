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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.TextOnlyTextInput
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.viewmodel.NoteEditorViewModel

@Composable
fun NoteEditorView(
    viewModel: NoteEditorViewModel,
    context: Context,
    navController: NavController,
    documentID: String
) {
    val titleTextValue = remember { mutableStateOf(viewModel.titleTextValue) }
    val contentTextValue = remember { mutableStateOf(viewModel.contentTextValue) }
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
                           navController.navigate(NavigationDestination.ProductivityActivity)
                        },
                        content = @Composable {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
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
                                    imageVector = Icons.Rounded.Save,
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
                                    imageVector = Icons.Rounded.ColorLens,
                                    contentDescription = stringResource(id = R.string.palette_icon_content_desc)
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
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = stringResource(id = R.string.delete_icon_content_desc)
                                )
                            }
                        )
                    }
                }
                TextOnlyTextInput(
                    textFieldValue = titleTextValue.value,
                    hint = stringResource(id = R.string.note_editor_title_placeholder),
                    textStyle = MaterialTheme.typography.h2,
                    modifier = Modifier.padding(start = 5.dp, top = 0.dp),
                    onValueChange = { newValue ->
                        titleTextValue.value = newValue
                    },
                )
                TextOnlyTextInput(
                    textFieldValue = contentTextValue.value,
                    hint = stringResource(id = R.string.note_editor_content_placeholder),
                    textStyle = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 5.dp),
                    onValueChange = { newValue ->
                        contentTextValue.value = newValue
                    }
                )
            }
        }
    }
}