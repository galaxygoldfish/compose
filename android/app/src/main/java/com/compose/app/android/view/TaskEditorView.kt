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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.ExperimentalTextOnlyTextField
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import com.compose.app.android.model.SubTaskDocument
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.viewmodel.TaskEditorViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun TaskEditorView(
    navController: NavController,
    documentID: String,
    viewModel: TaskEditorViewModel
) {

    val mainScaffoldState = rememberScaffoldState()
    val composeAsync = rememberCoroutineScope()

    val bottomSheetScaffoldState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val systemUiController = rememberSystemUiController()

    if (bottomSheetScaffoldState.isVisible) {
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colors.primaryVariant
        )
    } else {
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colors.background
        )
    }

    viewModel.apply {
        previousDocumentID.value = currentDocumentID.value
        currentDocumentID.value = documentID
        if (previousDocumentID.value != currentDocumentID.value) {
            titleTextFieldValue.value = TextFieldValue("")
            locationTextFieldValue.value = TextFieldValue("")
            updateTaskContents(navController.context)
            viewModel.interactionMonitor.value = false
        }
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
                    DatePickerSheetView(
                        viewModel = viewModel
                    )
                },
                sheetShape = RoundedCornerShape(8.dp),
                sheetBackgroundColor = MaterialTheme.colors.primaryVariant,
                sheetElevation = 0.dp,
                scrimColor = MaterialTheme.colors.surface.copy(0.5F)
            ) {
                Column {
                    TaskActionBar(
                        viewModel = viewModel,
                        bottomSheetScaffoldState = bottomSheetScaffoldState,
                        navController = navController
                    )
                    ExperimentalTextOnlyTextField(
                        textFieldValue = viewModel.titleTextFieldValue.value,
                        hint = stringResource(id = R.string.task_editor_title_placeholder),
                        onValueChange = {
                            viewModel.titleTextFieldValue.value = it
                        },
                        textStyle = MaterialTheme.typography.h2,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp)
                    )
                    Text(
                        text = if (!viewModel.interactionMonitor.value) {
                            stringResource(id = R.string.task_editor_due_date_null)
                        } else {
                            String.format(
                                stringResource(id = R.string.task_editor_due_date_template),
                                "${viewModel.currentMonth.value} ${viewModel.selectedDayIndex.value}, ${viewModel.currentYear.value}",
                                "${viewModel.selectedHour.value}:${viewModel.selectedMinute.value} ${if (viewModel.selectionAMPM.value == 0) "AM" else "PM"}"
                            )
                        },
                        color = MaterialTheme.colors.onBackground.copy(0.7F),
                        modifier = Modifier
                            .padding(start = 21.dp, top = 2.dp)
                            .clickable {
                                composeAsync.launch {
                                    bottomSheetScaffoldState.show()
                                }
                            }
                    )
                    Row(modifier = Modifier.padding(top = 20.dp)) {
                        Icon(
                            painter = painterResource(id = IconLocation),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                        ExperimentalTextOnlyTextField(
                            textFieldValue = viewModel.locationTextFieldValue.value,
                            hint = stringResource(id = R.string.task_editor_location_placeholder),
                            onValueChange = { newValue ->
                                viewModel.locationTextFieldValue.value = newValue
                            },
                            textStyle = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)
                        )
                    }
                    SubTaskListView(viewModel = viewModel)
                }
                StorageUsageAlertDialog(
                    showState = viewModel.showingStorageAlertDialog,
                    onDiscardAction = {
                        FirebaseDocument().deleteDocument(
                            documentID = viewModel.currentDocumentID.value!!,
                            documentType = DocumentType.TASK
                        )
                        navController.popBackStack()
                    },
                    navController = navController
                )
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun TaskActionBar(
    viewModel: TaskEditorViewModel,
    navController: NavController,
    bottomSheetScaffoldState: ModalBottomSheetState
) {
    val composeAsync = rememberCoroutineScope()
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
                composeAsync.launch {
                    if (viewModel.saveTaskData(navController.context)) {
                        navController.navigate(NavigationDestination.ProductivityView)
                    }
                }
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
            modifier = Modifier.padding(end = 5.dp, top = 5.dp)
        ) {
            Checkbox(
                checked = viewModel.taskCompletionState.value,
                onCheckedChange = {
                    FirebaseDocument().updateSpecificValue(
                        key = "COMPLETE",
                        newValue = it,
                        documentID = viewModel.currentDocumentID.value,
                        documentType = DocumentType.TASK
                    )
                    viewModel.taskCompletionState.value = it
                },
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 10.dp)
            )
            IconButton(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(30.dp),
                onClick = {
                    composeAsync.launch {
                        viewModel.saveTaskData(navController.context)
                    }
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
                    composeAsync.launch {
                        bottomSheetScaffoldState.show()
                    }
                },
                content = @Composable {
                    Icon(
                        painter = painterResource(id = IconNotification),
                        contentDescription = stringResource(id = R.string.notification_bell_content_desc)
                    )
                }
            )
            IconButton(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(30.dp),
                onClick = {
                    FirebaseDocument().deleteDocument(
                        documentID = viewModel.currentDocumentID.value!!,
                        documentType = DocumentType.TASK
                    )
                    navController.popBackStack()
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

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun SubTaskListView(
    viewModel: TaskEditorViewModel
) {
    Column(
        Modifier.scrollable(
            rememberScrollState(),
            Orientation.Vertical
        )
    ) {
        AnimatedVisibility(
            visible = viewModel.subTaskItemList.value.isNotEmpty()
        ) {
            LazyColumn(
                content = {
                    itemsIndexed(items = viewModel.subTaskItemList.value) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val currentItemComplete = remember { mutableStateOf(item.taskComplete) }
                            val currentItemText = remember {
                                mutableStateOf(
                                    TextFieldValue(
                                        item.taskName ?: ""
                                    )
                                )
                            }
                            Checkbox(
                                checked = currentItemComplete.value,
                                onCheckedChange = {
                                    viewModel.subTaskItemList.value[index].taskComplete = it
                                    currentItemComplete.value = it
                                }
                            )
                            val textStyle = MaterialTheme.typography.body1
                            ExperimentalTextOnlyTextField(
                                textFieldValue = currentItemText.value,
                                hint = stringResource(id = R.string.task_editor_sub_item_input_hint),
                                onValueChange = {
                                    viewModel.subTaskItemList.value[index].taskName =
                                        it.text
                                    currentItemText.value = it
                                },
                                textStyle = if (currentItemComplete.value) {
                                    textStyle.plus(
                                        TextStyle(
                                            textDecoration = TextDecoration.LineThrough,
                                            color = MaterialTheme.colors.onBackground.copy(
                                                0.5F
                                            )
                                        )
                                    )
                                } else {
                                    textStyle
                                },
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                    }
                },
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        // Since compose just *absolutely* refuses to update the list when button
        // is pressed, we use a dummy text view and a changing state to ensure
        // that it recomposes in a timely manner
        val dummyTextUpdate = remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .padding(top = 15.dp, start = 20.dp)
                .clickable {
                    viewModel.subTaskItemList.value.add(
                        SubTaskDocument(taskName = null, taskComplete = false)
                    )
                    dummyTextUpdate.value = !dummyTextUpdate.value
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_duotone_plus_round),
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
            Text(
                text = stringResource(id = R.string.task_editor_sub_item_button_text),
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(start = 15.dp)
            )
            // Dummy text view, transparent and as small as possible
            Text(
                text = dummyTextUpdate.value.toString(),
                fontSize = 1.sp,
                color = Color.Transparent
            )
        }
    }
}