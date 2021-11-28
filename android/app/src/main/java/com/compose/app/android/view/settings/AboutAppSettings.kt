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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.BuildConfig
import com.compose.app.android.R
import com.compose.app.android.components.SettingsActionBar
import com.compose.app.android.viewmodel.SettingsViewModel

@Composable
fun AboutAppSettings(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    Column {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_about_tag),
            navController = navController
        )
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.mipmap.compose_app_icon),
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
    }
}