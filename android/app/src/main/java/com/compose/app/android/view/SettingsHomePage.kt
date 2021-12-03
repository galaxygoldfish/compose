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

import android.text.format.Formatter
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.HomeSettingsItem
import com.compose.app.android.components.SettingsActionBar
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.utilities.createSquareImage
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.viewmodel.SettingsViewModel

@Composable
fun SettingsHomePage(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    viewModel.apply {
        setAvatarImage(LocalContext.current.filesDir.path)
        updateUserStorageUsage()
    }
    val dataStore = LocalContext.current.getDefaultPreferences()
    ComposeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            SettingsActionBar(
                title = stringResource(id = R.string.settings_home_page_title),
                navController = navController
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier
                        .padding(end = 20.dp, start = 20.dp, top = 15.dp)
                        .clickable {
                            navController.navigate(NavigationDestination.AccountSettings)
                        },
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = MaterialTheme.colors.primaryVariant,
                    elevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        viewModel.avatarImageStore.value?.let {
                            Image(
                                bitmap = it.createSquareImage().asImageBitmap(),
                                contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                modifier = Modifier
                                    .padding(15.dp)
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        Column {
                            dataStore.apply {
                                Text(
                                    text = """${getString("IDENTITY_USER_NAME_FIRST", "Error")!!} ${
                                        getString("IDENTITY_USER_NAME_LAST", "Error")
                                    }""",
                                    style = MaterialTheme.typography.h6,
                                    color = MaterialTheme.colors.onBackground
                                )
                                LinearProgressIndicator(
                                    progress = (viewModel.userStorageUsage.value / 5000000).toFloat(), // kind of broken lol
                                    modifier = Modifier
                                        .padding(end = 20.dp, top = 10.dp, bottom = 6.dp)
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
                                    color = MaterialTheme.colors.onBackground.copy(0.7F)
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.padding(top = 15.dp)
                ) {
                    HomeSettingsItem(
                        title = stringResource(id = R.string.settings_ui_customization_tag),
                        icon = painterResource(id = R.drawable.ic_duotone_theme_color),
                        background = TextColorRed,
                        onClick = { navController.navigate(NavigationDestination.CustomizationSettings) }
                    )
                    HomeSettingsItem(
                        title = stringResource(id = R.string.settings_account_tag),
                        icon = painterResource(id = R.drawable.ic_duotone_user_group),
                        background = TextColorPurple,
                        onClick = { navController.navigate(NavigationDestination.AccountSettings) }
                    )
                    HomeSettingsItem(
                        title = stringResource(id = R.string.settings_security_privacy_tag),
                        icon = painterResource(id = R.drawable.ic_duotone_lock_closed),
                        background = TextColorBlue,
                        onClick = { navController.navigate(NavigationDestination.SecurityPrivacySettings) }
                    )
                    HomeSettingsItem(
                        title = stringResource(id = R.string.settings_notifications_tag),
                        icon = painterResource(id = R.drawable.ic_duotone_notification_bell),
                        background = TextColorGreen,
                        onClick = { navController.navigate(NavigationDestination.NotificationSettings) }
                    )
                    HomeSettingsItem(
                        title = stringResource(id = R.string.settings_about_tag),
                        icon = painterResource(id = R.drawable.ic_duotone_info_circle),
                        background = TextColorYellow,
                        onClick = { navController.navigate(NavigationDestination.AboutAppSettings) }
                    )
                    HomeSettingsItem(
                        title = stringResource(id = R.string.settings_accessibility_tag),
                        icon = painterResource(id = R.drawable.ic_duotone_eye_open),
                        background = TextColorRed,
                        onClick = { navController.navigate(NavigationDestination.AccessibilitySettings) }
                    )
                    HomeSettingsItem(
                        title = stringResource(id = R.string.settings_help_feedback_tag),
                        icon = painterResource(id = R.drawable.ic_duotone_flag),
                        background = TextColorPurple,
                        onClick = { navController.navigate(NavigationDestination.HelpFeedbackSettings) }
                    )
                }
            }
        }
    }
}