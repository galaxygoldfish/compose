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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.*
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.theme.*
import com.compose.app.android.utilities.getCloudPreferences
import com.compose.app.android.viewmodel.SettingsViewModel
import com.godaddy.android.colorpicker.ClassicColorPicker

@ExperimentalGraphicsApi
@ExperimentalAnimationApi
@Composable
fun UICustomizationSettings(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_ui_customization_tag),
            navController = navController
        )
        Column(
            modifier = Modifier.padding(top = 15.dp)
        ) {
            LocalContext.current.apply {
                SwitchPreference(
                    title = stringResource(id = R.string.settings_customization_theme_title),
                    subtitle = stringResource(id = R.string.settings_customization_theme_subtitle),
                    icon = painterResource(id = IconThemeColor),
                    onAction = {
                        currentAppThemeState.value = it
                        startActivity(
                            Intent(this, ComposeBaseActivity::class.java)
                                .putExtra("SETTINGS_THEME_APPLY", true)
                        )
                    },
                    changeState = currentAppThemeState,
                    key = "STATE_DARK_MODE"
                )
                BasePreference(
                    title = stringResource(id = R.string.settings_customization_color_title),
                    icon = painterResource(id = IconColorSwitch),
                    body = stringResource(id = R.string.settings_customization_color_subtitle),
                    onClickAction = {
                        viewModel.showingColorPickerDialog.value = true
                    }
                )
            }
        }
        ColorPickerDialog(viewModel = viewModel)
    }
}

@ExperimentalGraphicsApi
@ExperimentalAnimationApi
@Composable
fun ColorPickerDialog(
    viewModel: SettingsViewModel
) {
    val colorCorrectedSecondary = MaterialTheme.colors.secondaryVariant.let {
        if (currentAppThemeState.value) it else it.copy(1.0F)
    }
    val tempAccentColor = remember { mutableStateOf(currentAppAccentColor.value) }
    AnimatedVisibility(
        visible = viewModel.showingColorPickerDialog.value
    ) {
        Dialog(
            onDismissRequest = {
                viewModel.showingColorPickerDialog.value = false
            }
        ) {
            DialogScaffold(
                text = stringResource(id = R.string.settings_customization_color_title),
                icon = painterResource(id = IconColorSwitch)
            ) {
                ClassicColorPicker(
                    onColorChanged = {
                        tempAccentColor.value = it.toColor()
                    },
                    color = tempAccentColor.value,
                    showAlphaBar = false,
                    modifier = Modifier
                        .height(250.dp)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                )
                LocalContext.current.apply {
                    FullWidthButton(
                        text = stringResource(id = R.string.settings_customization_color_button_positive),
                        icon = painterResource(id = IconCheckMark),
                        contentDescription = stringResource(id = R.string.welcome_log_in_button_content_desc),
                        color = colorCorrectedSecondary,
                        textStyle = MaterialTheme.typography.body2,
                        contentColor = MaterialTheme.colors.onBackground
                    ) {
                        getCloudPreferences().putInteger("STATE_ACCENT_COLOR", tempAccentColor.value.toArgb())
                        currentAppAccentColor.value = tempAccentColor.value
                        viewModel.showingColorPickerDialog.value = false
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}