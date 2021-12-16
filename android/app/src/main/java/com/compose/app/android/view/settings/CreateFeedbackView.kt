package com.compose.app.android.view.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.*
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.IconAlert
import com.compose.app.android.theme.IconHelpFeedback
import com.compose.app.android.theme.IconSend
import com.compose.app.android.utilities.getViewModel
import com.compose.app.android.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateFeedbackView(navController: NavController) {
    val viewModel = navController.context.getViewModel(SettingsViewModel::class.java)
    val hostState = rememberScaffoldState().snackbarHostState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_create_feedback_title),
            navController = navController
        )
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.settings_create_feedback_body),
                color = MaterialTheme.colors.onBackground.copy(0.8F),
                modifier = Modifier.padding(start = 23.dp, end = 20.dp, top = 5.dp, bottom = 10.dp)
            )
            LargeTextInputField(
                text = viewModel.currentFeedbackTitle.value,
                hint = stringResource(id = R.string.settings_create_feedback_input_title_hint),
                valueCallback = {
                    viewModel.currentFeedbackTitle.value = it
                },
                icon = painterResource(id = IconHelpFeedback),
                contentDescription = "",
                color = MaterialTheme.colors.primaryVariant
            )
            Card(
                shape = RoundedCornerShape(10.dp),
                backgroundColor = MaterialTheme.colors.primaryVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .padding(top = 5.dp)
                    .heightIn(max = 300.dp)
            ) {
                ExperimentalTextOnlyTextField(
                    textFieldValue = viewModel.currentFeedbackBody.value,
                    hint = stringResource(id = R.string.settings_create_feedback_input_body_hint),
                    onValueChange = {
                        viewModel.currentFeedbackBody.value = it
                    },
                    textStyle = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(
                        top = 10.dp,
                        start = 15.dp,
                        end = 15.dp,
                        bottom = 15.dp
                    )
                )
            }
            Text(
                text = stringResource(id = R.string.settings_create_feedback_type_header),
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 5.dp)
            )
            stringArrayResource(id = R.array.feedback_type).forEach { item ->
                val onClickAction = { viewModel.currentFeedbackType.value = item }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClickAction.invoke() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = viewModel.currentFeedbackType.value == item,
                        onClick = onClickAction,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Text(
                        text = item,
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicSnackbar(
                    hostState = hostState,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 5.dp,
                        top = 15.dp
                    ),
                    icon = painterResource(id = IconAlert),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.padding(top = 15.dp))
                // Must be initialized in @Composable function context as onClick is not
                val message = stringResource(id = R.string.settings_create_feedback_error_message)
                val successText = stringResource(id = R.string.settings_create_feedback_thanks_message)
                val coroutineScope = rememberCoroutineScope()
                FullWidthButton(
                    text = stringResource(id = R.string.settings_create_feedback_button_positive),
                    color = MaterialTheme.colors.primary,
                    icon = painterResource(id = IconSend),
                    contentDescription = "",
                    textStyle = MaterialTheme.typography.body1
                ) {
                    if (viewModel.currentFeedbackTitle.value.text.isNotEmpty()) {
                        viewModel.saveCurrentFeedback()
                        navController.navigate(NavigationDestination.ProductivityView)
                        Toast.makeText(navController.context, successText, Toast.LENGTH_LONG).show()
                    } else {
                        coroutineScope.launch {
                            hostState.showSnackbar(message)
                        }
                    }
                }
            }
        }
    }
}