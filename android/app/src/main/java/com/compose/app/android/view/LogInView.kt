package com.compose.app.android.view

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.BasicSnackbar
import com.compose.app.android.components.LargeTextInputField
import com.compose.app.android.components.TextOnlyButton
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.theme.IconAlert
import com.compose.app.android.theme.IconBackArrow
import com.compose.app.android.theme.IconEmail
import com.compose.app.android.theme.IconPassword
import com.compose.app.android.theme.IconPersonSingle
import com.compose.app.android.utilities.rawStringResource
import com.compose.app.android.viewmodel.LogInViewModel
import kotlinx.coroutines.launch

@Composable
fun LogInView(
    context: Context,
    viewModel: LogInViewModel,
    navController: NavController
) {

    val emailValue = remember { mutableStateOf(TextFieldValue()) }
    val passwordValue = remember { mutableStateOf(TextFieldValue()) }
    val scaffoldState = rememberScaffoldState()

    val snackbarIconState = remember { mutableStateOf(IconAlert) }
    val snackbarIconDescription = remember { mutableStateOf(context.rawStringResource(R.string.warning_icon_content_desc)) }

    fun showSnackbar(@StringRes stringID: Int) {
        viewModel.asyncScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                context.rawStringResource(stringID)
            )
        }
    }

    ComposeTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            },
            content = @Composable {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate(NavigationDestination.WelcomeActivity)
                            },
                            content = @Composable {
                                Icon(
                                    painter = painterResource(id = IconBackArrow),
                                    contentDescription = stringResource(id = R.string.back_button_content_desc),
                                    modifier = Modifier.padding(top = 20.dp, start = 10.dp),
                                )
                            },
                        )
                        Text(
                            text = stringResource(id = R.string.log_in_activity_title),
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.log_in_activity_subtitle),
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(start = 20.dp, top = 6.dp, end = 15.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.log_in_field_header_title),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(
                                top = 12.dp,
                                start = 22.dp,
                            )
                        )
                        LargeTextInputField(
                            text = emailValue.value,
                            hint = stringResource(id = R.string.log_in_activity_email_hint),
                            valueCallback = { emailValue.value = it },
                            icon = painterResource(id = IconEmail),
                            contentDescription = stringResource(id = R.string.email_icon_content_desc),
                            passwordType = false
                        )
                        LargeTextInputField(
                            text = passwordValue.value,
                            hint = stringResource(id = R.string.log_in_activity_password_hint),
                            valueCallback = { passwordValue.value = it },
                            icon = painterResource(id = IconPassword),
                            contentDescription = stringResource(id = R.string.lock_icon_content_desc),
                            passwordType = true
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 20.dp, top = 5.dp)
                            ) {
                                TextOnlyButton(
                                    text = stringResource(id = R.string.log_in_activity_action_cancel),
                                    color = colorResource(id = R.color.button_neutral_background_color),
                                    onClick = {
                                        navController.navigate(NavigationDestination.WelcomeActivity)
                                    }
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 20.dp, top = 5.dp)
                            ) {
                                TextOnlyButton(
                                    text = stringResource(id = R.string.log_in_activity_action_proceed),
                                    color = colorResource(id = R.color.deep_sea),
                                    onClick = {
                                        viewModel.attemptSignIn(
                                            emailValue.value,
                                            passwordValue.value,
                                            context,
                                            onSuccess = {
                                                navController.navigate(NavigationDestination.ProductivityActivity)
                                            },
                                            onFailure = {
                                                snackbarIconDescription.value = context.rawStringResource(R.string.warning_icon_content_desc)
                                                snackbarIconState.value = IconAlert
                                                showSnackbar(R.string.log_in_failure_message)
                                            },
                                            onPreLaunch = {
                                                snackbarIconDescription.value = context.rawStringResource(R.string.account_tree_icon_content_desc)
                                                snackbarIconState.value = IconPersonSingle
                                                showSnackbar(R.string.log_in_progress_message)
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                    BasicSnackbar(
                        hostState = scaffoldState.snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        icon = painterResource(id = snackbarIconState.value),
                        contentDescription = snackbarIconDescription.value
                    )
                }
            }
        )
    }
}