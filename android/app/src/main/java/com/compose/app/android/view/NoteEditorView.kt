package com.compose.app.android.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.ExperimentalTextOnlyTextField
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.viewmodel.NoteEditorViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

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
            titleTextValue.value = TextFieldValue("")
            contentTextValue.value = TextFieldValue("")
        }
    }

    val titleTextValue = remember { viewModel.titleTextValue }
    val contentTextValue = remember { viewModel.contentTextValue }

    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    val bottomSheetScaffoldState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val mainScaffoldState = rememberScaffoldState()

    if (bottomSheetScaffoldState.isVisible) {
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colors.primaryVariant
        )
    } else {
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colors.background
        )
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
                Column {
                    NoteActionBar(
                        viewModel = viewModel,
                        navController = navController,
                        bottomSheetScaffoldState = bottomSheetScaffoldState
                    )
                    ExperimentalTextOnlyTextField(
                        textFieldValue = titleTextValue.value,
                        hint = stringResource(id = R.string.note_editor_title_placeholder),
                        textStyle = MaterialTheme.typography.h2,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp),
                        onValueChange = { newValue ->
                            titleTextValue.value = newValue
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
                    ExperimentalTextOnlyTextField(
                        textFieldValue = contentTextValue.value,
                        hint = stringResource(id = R.string.note_editor_content_placeholder),
                        textStyle = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp),
                        onValueChange = { newValue ->
                            contentTextValue.value = newValue
                        }
                    )
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
}