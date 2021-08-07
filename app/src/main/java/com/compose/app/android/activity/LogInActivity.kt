package com.compose.app.android.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose.app.android.R
import com.compose.app.android.account.FirebaseAccount
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.utilities.rawStringResource
import com.compose.app.android.view.BasicSnackbar
import com.compose.app.android.view.LargeTextInputField
import com.compose.app.android.view.TextOnlyButton
import kotlinx.coroutines.*

class LogInActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }

    @Composable
    fun MainContent() {

        val emailValue = remember { mutableStateOf(TextFieldValue()) }
        val passwordValue = remember { mutableStateOf(TextFieldValue()) }
        val scaffoldState = rememberScaffoldState()

        ComposeTheme {
            Scaffold(
                scaffoldState = scaffoldState,
                snackbarHost = {
                    scaffoldState.snackbarHostState
                },
                content = @Composable {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            IconButton(
                                onClick = {
                                    startActivity(
                                        Intent(this@LogInActivity, WelcomeActivity::class.java)
                                    )
                                },
                                content = @Composable {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = stringResource(id = R.string.back_button_content_desc),
                                        modifier = Modifier.padding(top = 20.dp, start = 10.dp),
                                    )
                                },
                            )
                            Text(
                                text = stringResource(id = R.string.log_in_activity_title),
                                style = MaterialTheme.typography.h1,
                                modifier = Modifier.padding(start = 20.dp, top = 5.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.log_in_activity_subtitle),
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(start = 20.dp, top = 6.dp, end = 15.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.log_in_field_header_title),
                                style = MaterialTheme.typography.overline,
                                modifier = Modifier.padding(
                                    top = 12.dp,
                                    start = 22.dp,
                                )
                            )
                            LargeTextInputField(
                                text = emailValue.value,
                                hint = stringResource(id = R.string.log_in_activity_email_hint),
                                valueCallback = { emailValue.value = it },
                                icon = Icons.Rounded.Email,
                                contentDescription = stringResource(id = R.string.email_icon_content_desc),
                                passwordType = false
                            )
                            LargeTextInputField(
                                text = passwordValue.value,
                                hint = stringResource(id = R.string.log_in_activity_password_hint),
                                valueCallback = { passwordValue.value = it },
                                icon = Icons.Rounded.Lock,
                                contentDescription = stringResource(id = R.string.lock_icon_content_desc),
                                passwordType = true
                            )
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 20.dp, top = 5.dp)
                                ) {
                                    TextOnlyButton(
                                        text = stringResource(id = R.string.log_in_activity_action_cancel),
                                        color = colorResource(id = R.color.button_neutral_background_color),
                                        onClick = {
                                            onBackPressed()
                                        }
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 20.dp, top = 5.dp)
                                ) {
                                    TextOnlyButton(
                                        text = stringResource(id = R.string.log_in_activity_action_proceed),
                                        color = colorResource(id = R.color.deep_sea),
                                        onClick = {
                                            attemptSignIn(
                                                emailValue.value,
                                                passwordValue.value,
                                                scaffoldState
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        BasicSnackbar(
                            hostState = scaffoldState.snackbarHostState,
                            modifier = Modifier.align(
                                Alignment.BottomCenter
                            ),
                            icon = Icons.Rounded.Warning,
                            contentDescription = stringResource(id = R.string.warning_icon_content_desc)
                        )
                    }
                }
            )
        }
    }

    private fun attemptSignIn(emailState: TextFieldValue, passwordState: TextFieldValue, scaffoldState: ScaffoldState) {
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val synchronousScope = CoroutineScope(Dispatchers.Main + Job())
        asyncScope.launch {
            if (FirebaseAccount().authenticateWithEmail(emailState.text, passwordState.text, this@LogInActivity)) {
                startActivity(Intent(this@LogInActivity, ProductivityActivity::class.java))
            } else {
                synchronousScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(rawStringResource(R.string.log_in_failure_message))
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LogInPreview() {
    LogInActivity().MainContent()
}

