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
package com.compose.app.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.app.android.R
import com.compose.app.android.theme.currentAppThemeState

/**
 * Snackbar, containing an icon and text which is usually
 * displayed at the bottom of the screen
 * @param hostState - SnackbarHostState determining whether
 * to show the snackbar or not
 * @param modifier - Modifier to determine the placement of
 * the snackbar
 * @param icon - Painter containing the icon shown to the left
 * of the text displayed on the snackbar
 * @param contentDescription - Accessibility content description
 * of the icon used
 */
@Composable
fun BasicSnackbar(
    hostState: SnackbarHostState,
    modifier: Modifier,
    icon: Painter,
    contentDescription: String
) {
    SnackbarHost(
        modifier = modifier,
        hostState = hostState,
        snackbar = @Composable { data ->
            Snackbar(
                elevation = 0.dp,
                backgroundColor = if (currentAppThemeState.value) {
                    MaterialTheme.colors.primaryVariant
                } else {
                    MaterialTheme.colors.secondaryVariant
                },
                content = @Composable {
                    Row {
                        Icon(
                            painter = icon,
                            contentDescription = contentDescription,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            tint = MaterialTheme.colors.onBackground
                        )
                        Text(
                            text = data.message,
                            style = MaterialTheme.typography.body1,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 15.dp),
                            color = MaterialTheme.colors.onBackground
                        )
                    }
                },
            )
        },
    )
}

/**
 * Handle view used on the top of bottom sheet views
 */
@Composable
fun SheetHandle() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 15.dp)
                .width(100.dp)
                .height(4.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = colorResource(id = R.color.bottom_sheet_handle_color), // TODO - Don't use colorResource to fix weird jetpack compose dark/light theme switching
            elevation = 0.dp
        ) { }
    }
}

/**
 * A List item with text and an icon that can be used in
 * casual lists and context menus.
 * @param icon - The resource ID of the icon being used
 * in the current list item
 * @param contentDescription - Accessibility content
 * description of the icon being used
 * @param title - The text being shown in the current list
 * item
 * @param onClick - Invoked when the list item is clicked
 */
@Composable
fun OptionListItem(
    icon: Int,
    contentDescription: String,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                onClick.invoke()
            }
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun DialogScaffold(
    text: String,
    icon: Painter,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (currentAppThemeState.value) {
                    MaterialTheme.colors.primaryVariant
                } else {
                    MaterialTheme.colors.background
                }
            )
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 20.dp, start = 18.dp)
                .size(30.dp),
            tint = MaterialTheme.colors.onBackground
        )
        Text(
            text = text,
            style = MaterialTheme.typography.h4,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 20.dp, top = 5.dp),
            color = MaterialTheme.colors.onBackground
        )
        content.invoke()
    }
}

@Composable
fun colorCorrectedSecondary() : Color {
    return MaterialTheme.colors.secondaryVariant.let {
        if (currentAppThemeState.value) it else it.copy(1.0F)
    }
}