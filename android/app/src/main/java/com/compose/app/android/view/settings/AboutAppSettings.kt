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
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.compose.app.android.BuildConfig
import com.compose.app.android.R
import com.compose.app.android.components.BasePreference
import com.compose.app.android.components.DialogScaffold
import com.compose.app.android.components.SettingsActionBar
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.IconGift
import com.compose.app.android.theme.IconHelpFeedback
import com.compose.app.android.theme.IconSettings
import com.compose.app.android.theme.IconSuitcase
import com.compose.app.android.viewmodel.SettingsViewModel

@ExperimentalAnimationApi
@Composable
fun AboutAppSettings(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_about_tag),
            navController = navController
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .padding(start = 5.dp)
                    .size(80.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = true
                    )
            )
            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = String.format(
                        stringResource(id = R.string.settings_about_version_format),
                        BuildConfig.VERSION_NAME
                    ),
                    color = MaterialTheme.colors.onBackground.copy(0.5F)
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(top = 25.dp)
                .verticalScroll(rememberScrollState())
        ) {
            BasePreference(
                title = stringResource(id = R.string.settings_about_help_title),
                icon = painterResource(id = IconHelpFeedback),
                body = stringResource(id = R.string.settings_about_help_body),
                onClickAction = {
                    navController.navigate(NavigationDestination.HelpFeedbackSettings)
                }
            )
            BasePreference(
                title = stringResource(id = R.string.settings_about_whats_new_title),
                icon = painterResource(id = IconGift),
                body = stringResource(id = R.string.settings_about_whats_new_body),
                onClickAction = {
                    viewModel.showingWhatsNewDialog.value = true
                }
            )
            BasePreference(
                title = stringResource(id = R.string.settings_about_updates_title),
                icon = painterResource(id = IconSuitcase),
                body = stringResource(id = R.string.settings_about_updates_body),
                onClickAction = {
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                        navController.context.startActivity(this)
                    }
                }
            )
            BasePreference(
                title = stringResource(id = R.string.settings_about_manage_title),
                icon = painterResource(id = IconSettings),
                body = stringResource(id = R.string.settings_about_manage_body),
                onClickAction = {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        navController.context.startActivity(this)
                    }
                }
            )
        }
        WhatsNewDialog(viewModel = viewModel)
    }
}

@ExperimentalAnimationApi
@Composable
fun WhatsNewDialog(viewModel: SettingsViewModel) {
    AnimatedVisibility(
        visible = viewModel.showingWhatsNewDialog.value
    ) {
        Dialog(
            onDismissRequest = {
                viewModel.showingWhatsNewDialog.value = false
            }
        ) {
            DialogScaffold(
                text = stringResource(id = R.string.settings_about_whats_new_title),
                icon = painterResource(id = IconGift)
            ) {
                viewModel.currentUpdateChanges.value?.apply {
                    Text(
                        text = String.format(
                            stringResource(id = R.string.settings_about_whats_new_subtitle_format),
                            versionName,
                            changeDate
                        ),
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp),
                        color = MaterialTheme.colors.onBackground.copy(0.7F)
                    )
                    Text(
                        text = changeBody,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 20.dp),
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
        }
    }
}