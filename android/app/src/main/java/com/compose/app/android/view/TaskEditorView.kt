package com.compose.app.android.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.ExperimentalTextOnlyTextField
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.IconBackArrow
import com.compose.app.android.theme.IconNotification
import com.compose.app.android.theme.IconSaveContent
import com.compose.app.android.theme.IconTrashItem
import com.compose.app.android.viewmodel.TaskEditorViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
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

    val monthState = remember { mutableStateOf("") }
    val hourState = remember { mutableStateOf("") }
    val interactionMonitor = remember { mutableStateOf(false) }

    (navController.context as ComposeBaseActivity).apply {
        if (bottomSheetScaffoldState.isVisible) {
            window.navigationBarColor = resources.getColor(R.color.neutral_gray)
        } else {
            window.navigationBarColor = resources.getColor(R.color.text_color_reverse)
        }
    }

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
                    monthDayState = monthState,
                    timeHourState = hourState,
                    interactionMonitor = interactionMonitor
                )
            },
            sheetShape = RoundedCornerShape(8.dp),
            sheetBackgroundColor = colorResource(id = R.color.neutral_gray),
            sheetElevation = 0.dp,
            scrimColor = MaterialTheme.colors.surface.copy(0.5F)
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
                                painter = painterResource(id = IconBackArrow),
                                contentDescription = stringResource(id = R.string.back_button_content_desc)
                            )
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.padding(end = 5.dp, top = 5.dp)
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
                    textFieldValue = viewModel.titleTextFieldValue.value,
                    hint = stringResource(id = R.string.task_editor_title_placeholder),
                    onValueChange = {
                        viewModel.titleTextFieldValue.value = it
                    },
                    textStyle = MaterialTheme.typography.h2,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                )
                Text(
                    text = if (!interactionMonitor.value) {
                        stringResource(id = R.string.task_editor_due_date_null)
                    } else {
                        String.format(
                            stringResource(id = R.string.task_editor_due_date_template),
                            monthState.value,
                            hourState.value
                        )
                    },
                    color = colorResource(id = R.color.text_color_disabled),
                    modifier = Modifier
                        .padding(start = 21.dp, top = 2.dp)
                        .clickable {
                            composeAsync.launch {
                                bottomSheetScaffoldState.show()
                            }
                        }
                )
                Column(
                    Modifier.scrollable(
                        rememberScrollState(),
                        Orientation.Vertical
                    )
                ) {
                    ExperimentalTextOnlyTextField(
                        textFieldValue = viewModel.contentTextFieldValue.value,
                        hint = stringResource(id = R.string.task_editor_content_placeholder),
                        onValueChange = { newValue ->
                            viewModel.contentTextFieldValue.value = newValue
                        },
                        textStyle = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(start = 21.dp, top = 10.dp)
                    )
                }
            }
        }
    }
}

