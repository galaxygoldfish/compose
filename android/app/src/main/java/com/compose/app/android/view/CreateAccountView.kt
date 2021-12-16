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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.BasicSnackbar
import com.compose.app.android.components.IconOnlyButton
import com.compose.app.android.components.LargeTextInputField
import com.compose.app.android.components.TextOnlyButton
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.utilities.getViewModel
import com.compose.app.android.utilities.rawStringResource
import com.compose.app.android.viewmodel.CreateAccountViewModel

// TODO - move all this stuff to viewModel, text and image variables shoudln't be in the view code

@Composable
fun CreateAccountView(navController: NavController) {

    val viewModel = navController.context.getViewModel(CreateAccountViewModel::class.java)

    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }

    val firstNameState = remember { mutableStateOf(TextFieldValue()) }
    val lastNameState = remember { mutableStateOf(TextFieldValue()) }

    val scaffoldState = rememberScaffoldState()
    val snackbarIconState = remember { mutableStateOf(IconAlert) }
    val snackbarIconDescription = remember { mutableStateOf(navController.context.rawStringResource(R.string.warning_icon_content_desc)) }
    val avatarImageState = remember {
        mutableStateOf(
            BitmapFactory.decodeResource(
                navController.context.resources,
                R.drawable.default_avatar_image
            )
        )
    }

    val avatarImageUpdater = Observer<Bitmap> { avatar ->
        avatarImageState.value = avatar
    }

    viewModel.avatarImageLive.observe(navController.context as ComposeBaseActivity, avatarImageUpdater)

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
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate(NavigationDestination.WelcomeView)
                            },
                            modifier = Modifier.padding(top = 15.dp, start = 10.dp),
                            content = @Composable {
                                Icon(
                                    painter = painterResource(id = IconBackArrow),
                                    contentDescription = stringResource(id = R.string.back_button_content_desc),
                                )
                            },
                        )
                        Text(
                            text = stringResource(id = R.string.create_account_activity_title),
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(start = 20.dp, top = 0.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.create_account_activity_subtitle),
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(start = 20.dp, top = 6.dp, end = 15.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.create_account_account_header),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(
                                top = 20.dp,
                                start = 22.dp,
                            )
                        )
                        LargeTextInputField(
                            text = emailState.value,
                            hint = stringResource(id = R.string.create_account_email_address_hint),
                            valueCallback = { emailState.value = it },
                            icon = painterResource(id = IconEmail),
                            contentDescription = stringResource(id = R.string.email_icon_content_desc),
                            passwordType = false
                        )
                        LargeTextInputField(
                            text = passwordState.value,
                            hint = stringResource(id = R.string.create_account_password_hint),
                            valueCallback = { passwordState.value = it },
                            icon = painterResource(id = IconPassword),
                            contentDescription = stringResource(id = R.string.lock_icon_content_desc),
                            passwordType = true
                        )
                        Text(
                            text = stringResource(id = R.string.create_account_profile_header),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(
                                top = 18.dp,
                                start = 22.dp,
                            )
                        )
                        LargeTextInputField(
                            text = firstNameState.value,
                            hint = stringResource(id = R.string.create_account_first_name_hint),
                            valueCallback = { firstNameState.value = it },
                            icon = painterResource(id = IconPersonSingle),
                            contentDescription = stringResource(id = R.string.person_icon_content_desc),
                            passwordType = false
                        )
                        LargeTextInputField(
                            text = lastNameState.value,
                            hint = stringResource(id = R.string.create_account_last_name_hint),
                            valueCallback = { lastNameState.value = it },
                            icon = painterResource(id = IconPersonGroup),
                            contentDescription = stringResource(id = R.string.group_icon_content_desc),
                            passwordType = false
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = viewModel.avatarRowPadding.value),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.create_account_choose_profile_header),
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier.padding(top = 28.dp, start = 22.dp)
                                )
                                Row(
                                    modifier = Modifier.padding(start = 20.dp, top = 5.dp)
                                ) {
                                    IconOnlyButton(
                                        icon = painterResource(id = IconCamera),
                                        contentDescription = stringResource(id = R.string.file_folder_content_desc),
                                        onClick = {
                                            viewModel.openCameraForResult(navController.context as ComposeBaseActivity)
                                        },
                                    )
                                    IconOnlyButton(
                                        icon = painterResource(id = IconGallery),
                                        contentDescription = stringResource(id = R.string.camera_icon_content_desc),
                                        onClick = {
                                            viewModel.openGalleryForResult(navController.context as ComposeBaseActivity)
                                        },
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(end = 22.dp, top = 22.dp)
                            ) {
                                Image(
                                    bitmap = avatarImageState.value.asImageBitmap(),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(100.dp)
                                        .aspectRatio(1F),
                                    contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                    alignment = Alignment.Center
                                )
                            }
                        }
                        Box {
                            BasicSnackbar(
                                hostState = scaffoldState.snackbarHostState,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 10.dp,
                                        bottom = 10.dp
                                    ),
                                icon = painterResource(id = snackbarIconState.value),
                                contentDescription = snackbarIconDescription.value
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 25.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextOnlyButton(
                                text = stringResource(id = R.string.create_account_cancel_button),
                                color = MaterialTheme.colors.primaryVariant,
                                onClick = {
                                    navController.navigate(NavigationDestination.WelcomeView)
                                }
                            )
                            TextOnlyButton(
                                text = stringResource(id = R.string.create_account_continue_button),
                                color = MaterialTheme.colors.primary,
                                onClick = {
                                    (navController.context as ComposeBaseActivity).window.setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                    )
                                    viewModel.attemptCreateNewUser(
                                        emailState = emailState.value.text,
                                        passwordState = passwordState.value.text,
                                        nameState = firstNameState.value.text,
                                        lastNameState = lastNameState.value.text,
                                        snackbarState = scaffoldState.snackbarHostState,
                                        iconState = snackbarIconState,
                                        descriptionState = snackbarIconDescription,
                                        context = navController.context, // smh don't need this
                                        navController = navController
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}