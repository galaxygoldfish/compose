/**
 * Copyright (C) 2021  Sebastian Hriscu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **/
package com.compose.app.android.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.ExperimentalTextOnlyTextField
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import com.compose.app.android.model.SavedSpanType
import com.compose.app.android.model.SavedSpanType.COLOR_BLUE
import com.compose.app.android.model.SavedSpanType.COLOR_GREEN
import com.compose.app.android.model.SavedSpanType.COLOR_ORANGE
import com.compose.app.android.model.SavedSpanType.COLOR_PURPLE
import com.compose.app.android.model.SavedSpanType.COLOR_RED
import com.compose.app.android.model.SavedSpanType.COLOR_SPAN
import com.compose.app.android.model.SavedSpanType.COLOR_YELLOW
import com.compose.app.android.model.SavedSpanType.SIZE_SPAN
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.viewmodel.NoteEditorViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun NoteEditorView(
    viewModel: NoteEditorViewModel,
    navController: NavController,
    documentID: String
) {

    viewModel.apply {
        previousDocumentID.value = noteDocumentID.value
        noteDocumentID.value = documentID
        updateNoteContents()
        if (previousDocumentID.value != noteDocumentID.value) {
            clearTextFields()
        }
    }

    val systemUiController = rememberSystemUiController()
    val mainScaffoldState = rememberScaffoldState()
    val bottomSheetScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    if (bottomSheetScaffoldState.isVisible) {
        systemUiController.setNavigationBarColor(color = MaterialTheme.colors.primaryVariant)
    } else {
        systemUiController.setNavigationBarColor(color = MaterialTheme.colors.background)
    }

    ComposeTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = mainScaffoldState,
            snackbarHost = {
                mainScaffoldState.snackbarHostState
            }
        ) {
            ModalBottomSheetLayout(
                sheetState = bottomSheetScaffoldState,
                sheetContent = {
                    NoteColorPickerSheet(
                        currentNoteColor = viewModel.selectedNoteColorRes,
                        currentColorCentral = viewModel.selectedNoteColorCentral,
                        context = navController.context
                    )
                },
                sheetShape = RoundedCornerShape(8.dp),
                sheetBackgroundColor = MaterialTheme.colors.primaryVariant,
                sheetElevation = 0.dp,
                scrimColor = MaterialTheme.colors.surface.copy(0.5F)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    NoteActionBar(
                        viewModel = viewModel,
                        navController = navController,
                        bottomSheetScaffoldState = bottomSheetScaffoldState
                    )
                    ExperimentalTextOnlyTextField(
                        textFieldValue = viewModel.titleTextValue.value,
                        hint = stringResource(id = R.string.note_editor_title_placeholder),
                        textStyle = MaterialTheme.typography.h2,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp),
                        onValueChange = { newValue ->
                            viewModel.titleTextValue.value = newValue
                        },
                    )
                    Text(
                        text = String.format(
                            stringResource(id = R.string.note_option_menu_last_edited),
                            viewModel.getCurrentDate(),
                            viewModel.getCurrentTime()
                        ),
                        color = MaterialTheme.colors.onBackground.copy(0.7F),
                        modifier = Modifier.padding(top = 2.dp, start = 20.dp),
                        fontSize = 14.sp
                    )
                    Box(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        ExperimentalTextOnlyTextField(
                            textFieldValue = viewModel.contentTextValue.value,
                            hint = stringResource(id = R.string.note_editor_content_placeholder),
                            textStyle = MaterialTheme.typography.body2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, top = 10.dp, end = 15.dp)
                                .verticalScroll(rememberScrollState()),
                            onValueChange = {
                                viewModel.contentTextValue.value.let { old ->
                                    viewModel.contentTextValue.value = TextFieldValue(
                                        annotatedString = AnnotatedString(
                                            text = it.text,
                                            spanStyles = old.annotatedString.spanStyles,
                                            paragraphStyles = old.annotatedString.paragraphStyles
                                        ),
                                        selection = it.selection,
                                        composition = it.composition
                                    )
                                }
                            }
                        )
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Spacer(modifier = Modifier.fillMaxHeight(0.6F))
                            NoteFormatBar(viewModel = viewModel)
                        }
                    }
                    AnimatedVisibility(
                        visible = viewModel.showFormatOptionDialog.value
                    ) {
                        NoteFormatOptionDialog(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun NoteActionBar(
    viewModel: NoteEditorViewModel,
    navController: NavController,
    bottomSheetScaffoldState: ModalBottomSheetState
) {
    val coroutineScope = rememberCoroutineScope()
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
                navController.navigate(NavigationDestination.ProductivityView)
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
                    viewModel.saveNoteContents()
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
                    coroutineScope.launch {
                        bottomSheetScaffoldState.show()
                    }
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
                    FirebaseDocument().deleteDocument(
                        viewModel.noteDocumentID.value!!,
                        DocumentType.NOTE
                    )
                    viewModel.clearTextFields()
                    navController.navigate(NavigationDestination.ProductivityView)
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
}

@Composable
fun NoteFormatOptionDialog(
    viewModel: NoteEditorViewModel
) {
    val defaultColors = listOf(
        TextColorRed,
        TextColorOrange,
        TextColorYellow,
        TextColorGreen,
        TextColorBlue,
        TextColorPurple
    )
    Dialog(
        onDismissRequest = {
            viewModel.showFormatOptionDialog.value = false
        },
        content = {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.primaryVariant)
            ) {
                Icon(
                    painter = if (viewModel.currentDialogResource.value == 0) {
                        painterResource(id = R.drawable.ic_round_format_color_text_24)
                    } else painterResource(id = R.drawable.ic_round_format_size_24),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 20.dp, top = 25.dp)
                )
                Text(
                    text = if (viewModel.currentDialogResource.value == 0) {
                        stringResource(id = R.string.note_editor_format_dialog_title_color)
                    } else stringResource(id = R.string.note_editor_format_dialog_title_size),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(top = 10.dp, start = 20.dp, bottom = 25.dp)
                )
                val resourceList = if (viewModel.currentDialogResource.value == 0) {
                    stringArrayResource(id = R.array.format_text_color)
                } else stringArrayResource(id = R.array.format_text_size)
                LazyColumn {
                    itemsIndexed(resourceList) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 15.dp, start = 15.dp, bottom = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colors.secondaryVariant.copy(0.2F))
                                .clickable {
                                    if (viewModel.currentDialogResource.value == 0) {
                                        viewModel.addSpan(
                                            spanStyle = SpanStyle(color = defaultColors[index]),
                                            type = COLOR_SPAN,
                                            extra = when (index) {
                                                0 -> COLOR_RED
                                                1 -> COLOR_ORANGE
                                                2 -> COLOR_YELLOW
                                                3 -> COLOR_GREEN
                                                4 -> COLOR_BLUE
                                                5 -> COLOR_PURPLE
                                                else -> COLOR_RED
                                            }
                                        )
                                    } else {
                                        val textSize = (item.split(" ")[0].toInt())
                                        viewModel.addSpan(
                                            spanStyle = SpanStyle(fontSize = textSize.sp),
                                            type = SIZE_SPAN,
                                            extra = textSize
                                        )
                                    }
                                    viewModel.showFormatOptionDialog.value = false
                                },
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (viewModel.currentDialogResource.value == 0) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
                                        .clip(CircleShape)
                                        .size(30.dp)
                                        .background(defaultColors[index])
                                ) { }
                            }
                            Text(
                                text = item,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 15.dp))
            }
        }
    )
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun NoteFormatBar(viewModel: NoteEditorViewModel) {

    @Composable
    fun FormatBarItem(
        icon: Painter,
        onClick: () -> Unit
    ) {
        Box {
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                colors = buttonColors(
                    backgroundColor = MaterialTheme.colors.primaryVariant
                ),
                modifier = Modifier.padding(end = 10.dp),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null
                )
            }
        }
    }

    AnimatedVisibility(
        visible = viewModel.contentTextValue.value.selection.length > 0,
        enter = slideInHorizontally(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.background)
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 12.dp,
                    bottom = 10.dp
                )
                .horizontalScroll(rememberScrollState())
        ) {
            FormatBarItem(
                icon = painterResource(id = R.drawable.ic_round_format_color_text_24)
            ) {
                viewModel.apply {
                    currentDialogResource.value = 0
                    showFormatOptionDialog.value = true
                }
            }
            FormatBarItem(
                icon = painterResource(id = R.drawable.ic_round_format_bold_24)
            ) {
                viewModel.addSpan(
                    spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
                    type = SavedSpanType.BOLD_SPAN
                )
            }
            FormatBarItem(
                icon = painterResource(id = R.drawable.ic_round_format_italic_24)
            ) {
                viewModel.addSpan(
                    spanStyle = SpanStyle(fontStyle = FontStyle.Italic),
                    type = SavedSpanType.ITALIC_SPAN
                )
            }
            FormatBarItem(
                icon = painterResource(id = R.drawable.ic_round_format_underlined_24)
            ) {
                viewModel.addSpan(
                    spanStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                    type = SavedSpanType.UNDERLINE_SPAN
                )
            }
            FormatBarItem(
                icon = painterResource(id = R.drawable.ic_round_format_size_24)
            ) {
                viewModel.apply {
                    currentDialogResource.value = 1
                    showFormatOptionDialog.value = true
                }
            }
            FormatBarItem(
                icon = painterResource(id = R.drawable.ic_round_format_clear_24)
            ) {
                viewModel.addSpan(
                    spanStyle = SpanStyle(
                        color = if (currentAppThemeState.value) Color.White else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        textDecoration = TextDecoration.None
                    ),
                    type = SavedSpanType.NULL
                )
            }
        }
    }
}