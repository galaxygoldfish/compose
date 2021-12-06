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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.theme.IconBackspace
import com.compose.app.android.theme.IconCheckMark
import com.compose.app.android.theme.IconPassword
import com.compose.app.android.viewmodel.SecurityLockViewModel

@Composable
fun SecurityLockView(
    viewModel: SecurityLockViewModel,
    navController: NavController
) {
    ComposeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Icon(
                painter = painterResource(id = IconPassword),
                contentDescription = stringResource(id = R.string.lock_icon_content_desc),
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .padding(top = 40.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(30.dp)
            )
            Text(
                text = stringResource(id = R.string.security_lock_hint_text),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 30.dp)
            )
            Card(
                backgroundColor = MaterialTheme.colors.primaryVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 30.dp)
                    .defaultMinSize(minWidth = 200.dp, minHeight = 50.dp),
            ) {
                Text(
                    text = viewModel.passwordEnteredText.let {
                        var tempText = ""
                        repeat(it.value.text.length) { tempText += "*" }
                        return@let tempText
                    },
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            Column(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth().padding(top = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                @Composable
                fun PasswordButton(
                    text: AnnotatedString? = null,
                    onClickOverride: (() -> Unit)? = null,
                    icon: Painter? = null
                ) {
                    LocalHapticFeedback.current.let { haptics ->
                        Button(
                            onClick = {
                                if (onClickOverride == null) {
                                    viewModel.passwordEnteredText.value = TextFieldValue(
                                        text = viewModel.passwordEnteredText.value.text + text
                                    )
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                } else {
                                    onClickOverride.invoke()
                                }
                            },
                            shape = CircleShape,
                            elevation = ButtonDefaults.elevation(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primaryVariant
                            ),
                            modifier = Modifier.size(90.dp)
                        ) {
                            if (icon != null) {
                                Icon(
                                    painter = icon,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            } else {
                                Text(
                                    text = text!!,
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }
                val arrangement = Arrangement.SpaceEvenly
                val maxWidth = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 20.dp)
                Row(horizontalArrangement = arrangement, modifier = maxWidth) {
                    (1..3).forEach {
                        PasswordButton(text = AnnotatedString(it.toString()), null)
                    }
                }
                Row(horizontalArrangement = arrangement, modifier = maxWidth) {
                    (4..6).forEach {
                        PasswordButton(text = AnnotatedString(it.toString()), null)
                    }
                }
                Row(horizontalArrangement = arrangement, modifier = maxWidth) {
                    (7..9).forEach {
                        PasswordButton(text = AnnotatedString(it.toString()), null)
                    }
                }
                Row(
                    horizontalArrangement = arrangement,
                    modifier = maxWidth
                ) {
                    PasswordButton(
                        icon = painterResource(id = IconBackspace),
                        onClickOverride = {
                            viewModel.passwordEnteredText.apply {
                                this.value = TextFieldValue(this.value.text.substring(0, this.value.text.length - 1))
                            }
                        }
                    )
                    PasswordButton(text = AnnotatedString("0"), null)
                    PasswordButton(
                        icon = painterResource(id = IconCheckMark),
                        onClickOverride = {
                            viewModel.authenticate(navController.context).run {
                                if (this) {
                                    navController.navigate(NavigationDestination.ProductivityView)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
