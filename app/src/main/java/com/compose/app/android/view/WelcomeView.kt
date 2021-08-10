package com.compose.app.android.view

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.compose.app.android.R
import com.compose.app.android.presentation.CreateAccountActivity
import com.compose.app.android.presentation.LogInActivity
import com.compose.app.android.theme.ComposeTheme

@Composable
fun WelcomeView(context: Context) {
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
                    modifier = Modifier.padding(start = 20.dp, top = 36.dp)
                )
                Text(
                    text = stringResource(id = R.string.welcome_activity_preview_text),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 15.dp)
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
                    icon = Icons.Rounded.Person,
                    contentDescription = stringResource(id = R.string.welcome_log_in_button_content_desc),
                    color = colorResource(id = R.color.deep_sea),
                    onClick = {
                        context.startActivity(
                            Intent(context, LogInActivity::class.java)
                        )
                    }
                )
                FullWidthButton(
                    text = stringResource(id = R.string.welcome_button_create_account),
                    icon = Icons.Rounded.AddCircle,
                    contentDescription = stringResource(id = R.string.welcome_create_account_button_content_desc),
                    color = colorResource(id = R.color.button_neutral_background_color),
                    onClick = {
                        context.startActivity(
                            Intent(context, CreateAccountActivity::class.java)
                        )
                    }
                )
            }
        }
    }
}