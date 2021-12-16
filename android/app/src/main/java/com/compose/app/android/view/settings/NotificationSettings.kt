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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.SettingsActionBar
import com.compose.app.android.components.SwitchPreference
import com.compose.app.android.theme.IconNotification

@Composable
fun NotificationSettings(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_notifications_tag),
            navController = navController
        )
        Column(
            modifier = Modifier.padding(top = 15.dp)
        ) {
            LocalContext.current.apply {
                SwitchPreference(
                    title = stringResource(id = R.string.settings_notifications_enable_title),
                    subtitle = stringResource(id = R.string.settings_notifications_enable_body),
                    icon = painterResource(id = IconNotification),
                    key = "STATE_ENABLE_NOTIFICATIONS",
                    defaultValue = true
                )
            }
        }
    }
}