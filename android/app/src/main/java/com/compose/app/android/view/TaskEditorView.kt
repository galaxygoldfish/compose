package com.compose.app.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.ExperimentalTextOnlyTextField
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.IconBackArrow
import com.compose.app.android.viewmodel.TaskEditorViewModel

@ExperimentalMaterialApi
@Composable
fun TaskEditorView(
    navController: NavController,
    documentID: String,
    viewModel: TaskEditorViewModel
) {
    val mainScaffoldState = rememberScaffoldState()
    val bottomSheetScaffoldState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
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
                Text(text = "d")
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
                ExperimentalTextOnlyTextField(
                    textFieldValue = viewModel.contentTextFieldValue.value,
                    hint = stringResource(id = R.string.task_editor_content_placeholder),
                    onValueChange = { newValue ->
                        viewModel.contentTextFieldValue.value = newValue
                    },
                    textStyle = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                )
            }
        }
    }
}
