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
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.SettingsActionBar
import com.compose.app.android.components.SwitchPreference
import com.compose.app.android.theme.IconPassword
import com.compose.app.android.theme.currentAppThemeState
import com.compose.app.android.viewmodel.SettingsViewModel

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
        LocalContext.current.apply {
            // W I P
            SwitchPreference(
                title = stringResource(id = R.string.settings_customization_theme_title), //
                subtitle = stringResource(id = R.string.settings_customization_theme_subtitle), //
                icon = painterResource(id = IconPassword),
                onAction = {  },
                changeState = currentAppThemeState,
                key = "STATE_APP_SECURED"
            )
        }
    }
}