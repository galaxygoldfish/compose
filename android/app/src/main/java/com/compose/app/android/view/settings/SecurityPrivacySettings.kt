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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.*
import com.compose.app.android.theme.IconCheckMark
import com.compose.app.android.theme.IconEditPen
import com.compose.app.android.theme.IconPassword
import com.compose.app.android.utilities.getCloudPreferences
import com.compose.app.android.viewmodel.SettingsViewModel

@ExperimentalAnimationApi
@Composable
fun SecurityPrivacySettings(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_security_privacy_tag),
            navController = navController
        )
        Column(
            modifier = Modifier.padding(top = 15.dp)
        ) {
            LocalContext.current.apply {
                val enableLock =
                    remember { mutableStateOf(getCloudPreferences().getBoolean("STATE_APP_SECURED")) }
                SwitchPreference(
                    title = stringResource(id = R.string.settings_security_privacy_enable_title),
                    subtitle = stringResource(id = R.string.settings_security_privacy_enable_body),
                    icon = painterResource(id = IconPassword),
                    onAction = { enableLock.value = it },
                    key = "STATE_APP_SECURED",
                    changeState = enableLock
                )
                BasePreference(
                    title = stringResource(id = R.string.settings_security_privacy_lock_change_title),
                    icon = painterResource(id = IconEditPen),
                    body = stringResource(id = R.string.settings_security_privacy_lock_change_body),
                ) {
                    viewModel.showingPasswordEditDialog.value = true
                }
            }
        }
    }
    EditPasswordDialog(viewModel = viewModel)
}

@ExperimentalAnimationApi
@Composable
fun EditPasswordDialog(
    viewModel: SettingsViewModel
) {
    AnimatedVisibility(
        visible = viewModel.showingPasswordEditDialog.value
    ) {
        Dialog(
            onDismissRequest = {
                viewModel.showingPasswordEditDialog.value = false
            }
        ) {
            DialogScaffold(
                text = stringResource(id = R.string.settings_security_privacy_lock_change_title),
                icon = painterResource(id = IconEditPen)
            ) {
                LargeTextInputField(
                    text = viewModel.tempSecurityPin.value,
                    hint = stringResource(id = R.string.settings_security_privacy_lock_change_hint),
                    valueCallback = {
                        viewModel.tempSecurityPin.value = it
                    },
                    icon = painterResource(id = IconPassword),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                LocalContext.current.apply {
                    FullWidthButton(
                        text = stringResource(id = R.string.settings_security_privacy_lock_button_positive),
                        icon = painterResource(id = IconCheckMark),
                        contentDescription = "",
                        color = colorCorrectedSecondary(),
                        contentColor = MaterialTheme.colors.onBackground,
                        textStyle = MaterialTheme.typography.body1
                    ) {
                        viewModel.saveNewSecurityPin(this)
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}