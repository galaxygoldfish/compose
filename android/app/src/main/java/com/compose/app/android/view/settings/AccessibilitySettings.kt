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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.SettingsActionBar
import com.compose.app.android.theme.IconZoomIn
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.utilities.getViewModel
import com.compose.app.android.viewmodel.SettingsViewModel

@Composable
fun AccessibilitySettings(navController: NavController) {
    val viewModel = navController.context.getViewModel(SettingsViewModel::class.java)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_accessibility_tag),
            navController = navController
        )
        Column(
            modifier = Modifier.padding(top = 15.dp)
        ) {
            LocalContext.current.apply {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painter = painterResource(id = IconZoomIn),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 20.dp)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .fillMaxWidth(0.8F)
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_accessibility_text_size_title),
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.onBackground,
                            modifier = Modifier.padding(top = 15.dp)
                        )
                        Slider(
                            value = viewModel.accessibilityFontSize.value,
                            onValueChange = {
                                viewModel.accessibilityFontSize.value = it
                                getDefaultPreferences().edit().putString("STATE_FONT_SIZE", it.toString()).commit()
                            },
                            valueRange = 1.0F..2.0F,
                            steps = 10,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colors.onBackground,
                                activeTrackColor = MaterialTheme.colors.primary,
                                inactiveTrackColor = MaterialTheme.colors.primaryVariant
                            ),
                            modifier = Modifier.height(40.dp)
                        )
                    }
                }
            }
        }
    }
}