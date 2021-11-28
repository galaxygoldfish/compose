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

import android.content.Context
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.BasicSnackbar
import com.compose.app.android.components.LargeTextInputField
import com.compose.app.android.components.TextOnlyButton
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.utilities.rawStringResource
import com.compose.app.android.viewmodel.LogInViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
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
    val snackbarIconDescription =
        remember { mutableStateOf(context.rawStringResource(R.string.warning_icon_content_desc)) }

    fun showSnackbar(@StringRes stringID: Int) {
        viewModel.asyncScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                context.rawStringResource(stringID),
                duration = SnackbarDuration.Indefinite
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
                                navController.navigate(NavigationDestination.WelcomeView)
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
                        BasicSnackbar(
                            hostState = scaffoldState.snackbarHostState,
                            modifier = Modifier.padding(
                                start = 20.dp,
                                end = 20.dp,
                                bottom = 5.dp,
                                top = 15.dp
                            ),
                            icon = painterResource(id = snackbarIconState.value),
                            contentDescription = snackbarIconDescription.value
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 20.dp)
                            ) {
                                TextOnlyButton(
                                    text = stringResource(id = R.string.log_in_activity_action_cancel),
                                    color = MaterialTheme.colors.secondaryVariant,
                                    onClick = {
                                        navController.navigate(NavigationDestination.WelcomeView)
                                    }
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 20.dp)
                            ) {
                                TextOnlyButton(
                                    text = stringResource(id = R.string.log_in_activity_action_proceed),
                                    color = MaterialTheme.colors.primary,
                                    onClick = {
                                        (context as ComposeBaseActivity).window.setSoftInputMode(
                                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                        )
                                        viewModel.attemptSignIn(
                                            emailValue.value,
                                            passwordValue.value,
                                            context,
                                            onSuccess = {
                                                navController.navigate(NavigationDestination.ProductivityView)
                                            },
                                            onFailure = {
                                                snackbarIconDescription.value =
                                                    context.rawStringResource(R.string.warning_icon_content_desc)
                                                snackbarIconState.value = IconAlert
                                                showSnackbar(R.string.log_in_failure_message)
                                            },
                                            onPreLaunch = {
                                                snackbarIconDescription.value =
                                                    context.rawStringResource(R.string.account_tree_icon_content_desc)
                                                snackbarIconState.value = IconPersonSingle
                                                showSnackbar(R.string.log_in_progress_message)
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}