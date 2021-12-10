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

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.SettingsActionBar
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.viewmodel.SettingsViewModel

@Composable
fun HelpFeedbackSettings(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        SettingsActionBar(
            title = stringResource(id = R.string.settings_help_feedback_tag),
            navController = navController
        )
        Column(
            modifier = Modifier.padding(top = 25.dp)
        ) {
            HelpFeedbackCard(
                text = stringResource(id = R.string.settings_help_feedback_support_title),
                body = stringResource(id = R.string.settings_help_feedback_support_body),
                image = painterResource(id = R.drawable.ic_illustration_help),
                onClickAction = {
                    navController.context.let {
                        Toast.makeText(
                            it,
                            it.resources.getString(R.string.settings_feature_unavailable_toast),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
            HelpFeedbackCard(
                text = stringResource(id = R.string.settings_help_feedback_feedback_title),
                body = stringResource(id = R.string.settings_help_feedback_feedback_body),
                image = painterResource(id = R.drawable.ic_illustration_feedback),
                onClickAction = {
                    navController.navigate(NavigationDestination.CreateFeedbackView)
                }
            )
        }
    }
}

@Composable
fun HelpFeedbackCard(
    text: String,
    body: String,
    image: Painter,
    onClickAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = MaterialTheme.colors.primaryVariant,
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 19.dp, end = 19.dp, bottom = 15.dp)
            .clickable {
                onClickAction.invoke()
            }
    ) {
        Row {
            Column(
                modifier = Modifier.fillMaxWidth(0.6F)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 15.dp),
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = body,
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                    color = MaterialTheme.colors.onBackground.copy(0.8F)
                )
            }

            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
            )
        }
    }
}