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
package com.compose.app.android.view.settings

import android.content.Intent
import android.text.format.Formatter
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.*
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.utilities.createSquareImage
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.viewmodel.SettingsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@ExperimentalAnimationApi
@Composable
fun AccountSettings(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    if (viewModel.avatarImageStore.value == null) {
        viewModel.setAvatarImage(LocalContext.current.filesDir.path)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_account_tag),
            navController = navController
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            viewModel.avatarImageStore.value?.let {
                Image(
                    bitmap = it.createSquareImage().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 20.dp, top = 30.dp)
                        .size(110.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier.padding(start = 30.dp, top = 10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    LocalContext.current.getDefaultPreferences().apply {
                        Text(
                            text = getString("IDENTITY_USER_NAME_FIRST", "Error")!!,
                            style = MaterialTheme.typography.h3,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.onBackground
                        )
                        Text(
                            text = getString("IDENTITY_USER_NAME_LAST", "Error")!!,
                            style = MaterialTheme.typography.h3,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colors.onBackground
                        )
                        Text(
                            text = stringResource(id = R.string.settings_account_type_temp),
                            color = MaterialTheme.colors.onBackground.copy(0.7F)
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp)
        ) {
            AccountSettingsItem(
                icon = painterResource(id = IconStorage),
                title = stringResource(id = R.string.settings_account_storage_header),
                onClick = { /** go to storage settings **/ },
                paddingTop = 10.dp
            ) {
                LinearProgressIndicator(
                    progress = (viewModel.userStorageUsage.value / 5000000).toFloat(), // again, broken
                    modifier = Modifier
                        .padding(end = 10.dp, top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Text(
                    text = String.format(
                        stringResource(id = R.string.profile_context_menu_storage_template),
                        Formatter.formatFileSize(
                            navController.context,
                            viewModel.userStorageUsage.value.toLong()
                        )
                    ),
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onBackground.copy(0.7F),
                    modifier = Modifier.padding(top = 10.dp, bottom = 15.dp)
                )
            }
            AccountSettingsItem(
                icon = painterResource(id = IconEmail),
                title = stringResource(id = R.string.settings_account_email_header),
                onClick = {
                    navController.context.apply {
                        Toast.makeText(
                            this,
                            getString(R.string.settings_account_email_error_message),
                            LENGTH_LONG
                        ).show()
                    }
                },
                paddingTop = 20.dp
            ) {
                Firebase.auth.currentUser?.let { user ->
                    Text(
                        text = user.email ?: "Error",
                        color = MaterialTheme.colors.onBackground.copy(0.7F),
                        modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                    )
                }
            }
            AccountSettingsItem(
                icon = painterResource(id = IconPassword),
                title = stringResource(id = R.string.settings_account_password_header),
                onClick = {
                    viewModel.showingPasswordDialog.value = true
                },
                paddingTop = 10.dp
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = viewModel.getPasswordHidden(LocalContext.current),
                        color = MaterialTheme.colors.onBackground.copy(0.7F),
                        modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                    )
                    Icon(
                        painter = painterResource(id = IconRightArrowSmall),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 15.dp)
                    )
                }
            }
            AccountSettingsItem(
                icon = painterResource(id = IconEditPen),
                title = stringResource(id = R.string.settings_account_edit_header),
                onClick = {
                    viewModel.showingEditAccountDialog.value = true
                },
                paddingTop = 20.dp
            ) {
                Text(
                    text = stringResource(id = R.string.settings_account_edit_body),
                    color = MaterialTheme.colors.onBackground.copy(0.7F),
                    modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                )
            }
            AccountSettingsItem(
                icon = painterResource(id = IconLogIn),
                title = stringResource(id = R.string.settings_account_log_out_header),
                onClick = { viewModel.showingLogOutDialog.value = true },
                paddingTop = 20.dp
            ) {
                Text(
                    text = stringResource(id = R.string.settings_account_log_out_body),
                    color = MaterialTheme.colors.onBackground.copy(0.7F),
                    modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                )
            }
            AccountSettingsItem(
                icon = painterResource(id = IconTrashItem),
                title = stringResource(id = R.string.settings_account_delete_header),
                onClick = {
                    viewModel.showingAccountDeleteDialog.value = true
                },
                paddingTop = 10.dp
            ) {
                Text(
                    text = stringResource(id = R.string.settings_account_delete_body),
                    color = MaterialTheme.colors.onBackground.copy(0.7F),
                    modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
                )
            }
        }
        LogOutAccountDialog(
            navController = navController,
            showingDialog = viewModel.showingLogOutDialog
        )
        EditAccountDetailsDialog(viewModel = viewModel)
        PasswordChangeDialog(viewModel = viewModel)
        AccountDeleteDialog(
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
fun AccountSettingsItem(
    icon: Painter,
    title: String,
    paddingTop: Dp? = 20.dp,
    onClick: (() -> Unit)? = { },
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = paddingTop!!)
            .clickable { onClick?.invoke() },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.padding(20.dp)
            )
            Column(
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(top = 10.dp)
                )
                content.invoke()
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun LogOutAccountDialog(
    navController: NavController,
    showingDialog: MutableState<Boolean>
) {
    AnimatedVisibility(
        visible = showingDialog.value
    ) {
        Dialog(
            onDismissRequest = { showingDialog.value = false }
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (currentAppThemeState.value) {
                            MaterialTheme.colors.primaryVariant
                        } else {
                            MaterialTheme.colors.background
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(id = IconPassword),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 20.dp, start = 18.dp)
                        .size(30.dp),
                    tint = MaterialTheme.colors.onBackground
                )
                Text(
                    text = stringResource(id = R.string.dialog_log_out_header),
                    style = MaterialTheme.typography.h4,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 5.dp),
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = stringResource(id = R.string.dialog_log_out_body_text),
                    modifier = Modifier.padding(start = 20.dp, top = 5.dp, end = 15.dp),
                    color = MaterialTheme.colors.onBackground
                )
                Column(
                    modifier = Modifier.padding(bottom = 15.dp, top = 20.dp)
                ) {
                    LocalContext.current.let { context ->
                        FullWidthButton(
                            text = stringResource(id = R.string.dialog_log_out_button_positive),
                            icon = painterResource(id = IconCheckMark),
                            contentDescription = stringResource(id = R.string.welcome_log_in_button_content_desc),
                            color = MaterialTheme.colors.secondaryVariant.let {
                                if (currentAppThemeState.value) it else it.copy(1.0F)
                            },
                            textStyle = MaterialTheme.typography.body2,
                            contentColor = MaterialTheme.colors.onBackground
                        ) {
                            FirebaseAccount().signOutUser(context)
                            context.startActivity(
                                Intent(context, ComposeBaseActivity::class.java)
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun EditAccountDetailsDialog(
    viewModel: SettingsViewModel
) {
    val colorCorrectedSecondary = MaterialTheme.colors.secondaryVariant.let {
        if (currentAppThemeState.value) it else it.copy(1.0F)
    }
    if (viewModel.tempFirstName.value.text.isEmpty()) {
        viewModel.updateMetadata(LocalContext.current)
    }
    AnimatedVisibility(
        visible = viewModel.showingEditAccountDialog.value
    ) {
        Dialog(
            onDismissRequest = {
                viewModel.showingEditAccountDialog.value = false
            }
        ) {
            DialogScaffold(
                text = stringResource(id = R.string.settings_account_edit_header),
                icon = painterResource(id = IconEditPen)
            ) {
                LocalContext.current.let { context ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        viewModel.tempAvatarImage.value?.let {
                            Image(
                                bitmap = it.createSquareImage().asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(
                                        start = 20.dp,
                                        top = 30.dp,
                                        end = 20.dp,
                                        bottom = 20.dp
                                    )
                                    .size(110.dp)
                                    .clip(CircleShape)
                            )
                            IconOnlyButton(
                                icon = painterResource(id = IconCamera),
                                contentDescription = stringResource(id = R.string.file_folder_content_desc),
                                onClick = {
                                    viewModel.openCameraForResult(context)
                                },
                                color = colorCorrectedSecondary
                            )
                            IconOnlyButton(
                                icon = painterResource(id = IconGallery),
                                contentDescription = stringResource(id = R.string.camera_icon_content_desc),
                                onClick = {
                                    viewModel.openGalleryForResult(context)
                                },
                                color = colorCorrectedSecondary
                            )
                        }
                    }
                    LargeTextInputField(
                        text = viewModel.tempFirstName.value,
                        hint = stringResource(id = R.string.settings_account_edit_name_hint),
                        valueCallback = {
                            viewModel.tempFirstName.value = it
                        },
                        icon = painterResource(id = IconPersonSingle),
                        contentDescription = stringResource(id = R.string.person_icon_content_desc),
                        contentColor = MaterialTheme.colors.onBackground,
                        color = colorCorrectedSecondary
                    )
                    LargeTextInputField(
                        text = viewModel.tempLastName.value,
                        hint = stringResource(id = R.string.settings_account_edit_surname_hint),
                        valueCallback = {
                            viewModel.tempLastName.value = it
                        },
                        icon = painterResource(id = IconPersonGroup),
                        contentDescription = stringResource(id = R.string.person_icon_content_desc),
                        contentColor = MaterialTheme.colors.onBackground,
                        color = colorCorrectedSecondary
                    )
                    Spacer(modifier = Modifier.padding(top = 15.dp))
                    LocalContext.current.apply {
                        FullWidthButton(
                            text = stringResource(id = R.string.settings_account_edit_positive_button),
                            icon = painterResource(id = IconCheckMark),
                            contentDescription = stringResource(id = R.string.welcome_log_in_button_content_desc),
                            color = colorCorrectedSecondary,
                            textStyle = MaterialTheme.typography.body2,
                            contentColor = MaterialTheme.colors.onBackground
                        ) {
                            viewModel.apply {
                                updateAccountEdit(context)
                                setAvatarImage(filesDir.path, true)
                                showingEditAccountDialog.value = false
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(bottom = 10.dp))
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun PasswordChangeDialog(
    viewModel: SettingsViewModel
) {
    AnimatedVisibility(
        visible = viewModel.showingPasswordDialog.value
    ) {
        Dialog(
            onDismissRequest = {
                viewModel.showingPasswordDialog.value = false
            }
        ) {
            DialogScaffold(
                text = stringResource(id = R.string.settings_account_password_header),
                icon = painterResource(id = IconPassword)
            ) {
                LargeTextInputField(
                    text = viewModel.tempPassword.value,
                    hint = stringResource(id = R.string.log_in_activity_password_hint),
                    valueCallback = {
                        viewModel.tempPassword.value = it
                    },
                    icon = painterResource(id = IconPassword),
                    contentDescription = stringResource(id = R.string.lock_icon_content_desc),
                    contentColor = MaterialTheme.colors.onBackground,
                    passwordType = true,
                    color = colorCorrectedSecondary()
                )
                Spacer(modifier = Modifier.padding(top = 15.dp))
                LocalContext.current.let { context ->
                    FullWidthButton(
                        text = stringResource(id = R.string.settings_account_edit_positive_button),
                        icon = painterResource(id = IconCheckMark),
                        contentDescription = stringResource(id = R.string.welcome_log_in_button_content_desc),
                        color = colorCorrectedSecondary(),
                        textStyle = MaterialTheme.typography.body2,
                        contentColor = MaterialTheme.colors.onBackground
                    ) {
                        viewModel.apply {
                            if (tempPassword.value.text.length > 4) {
                                updatePasswordEdit(context)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.settings_account_password_error_message),
                                    LENGTH_LONG
                                ).show()
                            }
                            showingPasswordDialog.value = false
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(bottom = 10.dp))
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun AccountDeleteDialog(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    val doneDeletingAccount = remember { mutableStateOf(false) }
    val accountDeleteProcessing = remember { mutableStateOf(false) }
    AnimatedVisibility(
        visible = viewModel.showingAccountDeleteDialog.value
    ) {
        Dialog(
            onDismissRequest = {
                viewModel.showingAccountDeleteDialog.value = false
            }
        ) {
            DialogScaffold(
                text = stringResource(id = R.string.settings_account_delete_header),
                icon = painterResource(id = IconTrashItem)
            ) {
                Text(
                    text = stringResource(id = R.string.settings_account_delete_warning),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp)
                )
                LocalContext.current.apply {
                    Button(
                        modifier = Modifier
                            .height(height = 60.dp)
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        onClick = {
                            accountDeleteProcessing.value = true
                            FirebaseAccount().deleteAccount(
                                context = this,
                                monitor = doneDeletingAccount
                            )
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorCorrectedSecondary()),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            if (!accountDeleteProcessing.value) {
                                Icon(
                                    painter = painterResource(id = IconCheckMark),
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.onBackground
                                )
                                Text(
                                    text = stringResource(id = R.string.settings_account_delete_positive_button),
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 1.dp, start = 24.dp),
                                    color = MaterialTheme.colors.onBackground,
                                )
                            } else {
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(0.8F),
                                    color = currentAppAccentColor.value
                                )
                                if (doneDeletingAccount.value) {
                                    navController.navigate(NavigationDestination.WelcomeView)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}