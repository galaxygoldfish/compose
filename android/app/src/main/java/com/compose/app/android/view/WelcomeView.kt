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

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.FullWidthButton
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.theme.IconCreateAccount
import com.compose.app.android.theme.IconLogIn

@Composable
fun WelcomeView(context: Context, navController: NavController) {
    ComposeTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_activity_title),
                    style = MaterialTheme.typography.h1,
                    modifier = Modifier.padding(start = 20.dp, top = 36.dp),
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = stringResource(id = R.string.welcome_activity_preview_text),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 15.dp),
                    color = MaterialTheme.colors.onSurface
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_welcome_screen_graphic),
                contentDescription = stringResource(id = R.string.welcome_graphic_content_desc),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 45.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                FullWidthButton(
                    text = stringResource(id = R.string.welcome_button_log_in),
                    icon = painterResource(id = IconLogIn),
                    contentDescription = stringResource(id = R.string.welcome_log_in_button_content_desc),
                    color = colorResource(id = R.color.deep_sea),
                    onClick = {
                        navController.navigate(NavigationDestination.LogInView)
                    }
                )
                FullWidthButton(
                    text = stringResource(id = R.string.welcome_button_create_account),
                    icon = painterResource(id = IconCreateAccount),
                    contentDescription = stringResource(id = R.string.welcome_create_account_button_content_desc),
                    color = colorResource(id = R.color.button_neutral_background_color),
                    onClick = {
                        navController.navigate(NavigationDestination.CreateAccountView)
                    }
                )
            }
        }
    }
}