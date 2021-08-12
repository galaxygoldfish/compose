package com.compose.app.android.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.compose.app.android.R
import com.compose.app.android.presentation.NoteEditorActivity
import com.compose.app.android.theme.ComposeTheme

@Composable
fun NoteEditorView(
    viewModel: ViewModel,
    context: Context
) {
    ComposeTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        (context as NoteEditorActivity).onBackPressed()
                    },
                    content = @Composable {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_content_desc)
                        )
                    }
                )
                Row {
                    IconButton(
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
                        onClick = {

                        },
                        content = @Composable {
                            Icon(
                                imageVector = Icons.Rounded.Palette,
                                contentDescription = stringResource(id = R.string.palette_icon_content_desc)
                            )
                        }
                    )
                    IconButton(
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
        }
    }
}