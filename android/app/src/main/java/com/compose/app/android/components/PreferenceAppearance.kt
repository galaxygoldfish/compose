package com.compose.app.android.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.theme.IconBackArrow
import com.compose.app.android.theme.currentAppThemeState
import com.compose.app.android.utilities.getCloudPreferences

/**
 * "Link preference" used only on the settings home page,
 * for large preference categories. Has a colored box with
 * an icon in it and a title label.
 *
 * @param title - The label to be displayed on the preference
 * @param icon - The icon to be displayed inside the colored
 * box
 * @param background - Color used to paint the colored icon box
 * @param onClick - Unit returning function called when preference
 * is clicked
 */
@Composable
fun HomeSettingsItem(
    title: String,
    icon: Painter,
    background: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() }
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(background.copy(if (currentAppThemeState.value) 0.9F else 0.5F))
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 20.dp),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onBackground
        )
    }
}

/**
 * Action bar used on all settings screens, with a large
 * bold title and a simple back button
 *
 * @param title - The title displayed on the action bar
 * @param navController - NavController used to trigger
 * back action when button is pressed
 */
@Composable
fun SettingsActionBar(
    title: String,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(start = 10.dp, top = 20.dp)
        ) {
            Icon(
                painter = painterResource(id = IconBackArrow),
                contentDescription = stringResource(id = R.string.back_button_content_desc),
                tint = MaterialTheme.colors.onBackground
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.h3,
            modifier = Modifier.padding(start = 23.dp),
            color = MaterialTheme.colors.onBackground
        )
    }
}

/**
 * Basic informational preference with only title and body
 * text accompanied with an icon, no changeable state. This
 * is usually used to link to a dialog or other screen.
 *
 * @param title - The main text to be shown on the preference.
 * @param body - The text to be displayed below the title, used
 * as a description of the preference.
 * @param icon - The icon to be shown to the left of the title and
 * body.
 * @param onClickAction - Code block to be invoked when the
 * preference is clicked.
 */
@Composable
fun BasePreference(
    title: String,
    icon: Painter,
    body: String,
    onClickAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickAction.invoke() }
            .padding(end = 20.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp)
        )
        Column(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(top = 15.dp)
            )
            Text(
                text = body,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground.copy(0.7F),
                modifier = Modifier.padding(bottom = 15.dp)
            )
        }
    }
}

/**
 * Basic switch preference with icon, title and subtitle. Must
 * be called with context or in an apply block with context to
 * access SharedPreferences.
 *
 * @param title - The main title of the preference
 * @param subtitle - The text to be displayed below the title, at
 * a lower opacity
 * @param icon - An icon to be displayed to the left of the title
 * and subtitle text
 * @param onAction - Callback with boolean lambda value invoked
 * when the preference or switch is clicked, new value is passed in
 * @param key - The key to save this preference value as in
 * SharedPreferences
 */
@Composable
fun Context.SwitchPreference(
    title: String,
    subtitle: String,
    icon: Painter,
    onAction: (Boolean) -> Unit,
    changeState: MutableState<Boolean>,
    key: String
) {
    val cloudPreferences = this.getCloudPreferences()
    val onClickAction = { newVal: Boolean ->
        cloudPreferences.putBoolean(key, newVal)
        onAction.invoke(newVal)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            onClickAction.invoke(!changeState.value)
        }
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp)
        )
        Column(
            modifier = Modifier.padding(start = 20.dp)
                .fillMaxWidth(0.8F)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(top = 15.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground.copy(0.7F),
                modifier = Modifier.padding(bottom = 15.dp)
            )
        }
        MaterialTheme.colors.apply {
            Switch(
                checked = changeState.value,
                onCheckedChange = onClickAction,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = primary,
                    uncheckedThumbColor = Color.DarkGray.copy(0.8F), // TODO - bad for accessibility
                    checkedTrackColor = primary.copy(0.8F),
                    uncheckedTrackColor = Color.DarkGray.copy(0.6F) // TODO - bad for accessibility
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp, end = 20.dp)
            )
        }
    }
}