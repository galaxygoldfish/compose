package com.compose.app.android.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.compose.app.android.R
import com.compose.app.android.presentation.CreateAccountActivity
import com.compose.app.android.presentation.WelcomeActivity
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.utilities.rawStringResource
import com.compose.app.android.viewmodel.CreateAccountViewModel

@Composable
fun CreateAccountView(context: Context, viewModel: CreateAccountViewModel) {

    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }

    val firstNameState = remember { mutableStateOf(TextFieldValue()) }
    val lastNameState = remember { mutableStateOf(TextFieldValue()) }

    val scaffoldState = rememberScaffoldState()
    val snackbarIconState = remember { mutableStateOf(Icons.Rounded.Warning) }
    val snackbarIconDescription = remember { mutableStateOf(context.rawStringResource(R.string.warning_icon_content_desc)) }
    val avatarImageState = remember { mutableStateOf(BitmapFactory.decodeResource(context.resources, R.drawable.default_avatar_image)) }

    val createAccountActivity = (context as CreateAccountActivity)

    val avatarImageUpdater = Observer<Bitmap> { avatar ->
        avatarImageState.value = avatar
    }

    viewModel.avatarImageLive.observe(context, avatarImageUpdater)

    ComposeTheme(false) {
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
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        IconButton(
                            onClick = {
                                context.startActivity(
                                    Intent(context, WelcomeActivity::class.java)
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
                            text = stringResource(id = R.string.create_account_activity_title),
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.create_account_activity_subtitle),
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(start = 20.dp, top = 6.dp, end = 15.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.create_account_account_header),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(
                                top = 20.dp,
                                start = 22.dp,
                            )
                        )
                        LargeTextInputField(
                            text = emailState.value,
                            hint = stringResource(id = R.string.create_account_email_address_hint),
                            valueCallback = { emailState.value = it },
                            icon = Icons.Rounded.Email,
                            contentDescription = stringResource(id = R.string.email_icon_content_desc),
                            passwordType = false
                        )
                        LargeTextInputField(
                            text = passwordState.value,
                            hint = stringResource(id = R.string.create_account_password_hint),
                            valueCallback = { passwordState.value = it },
                            icon = Icons.Rounded.Lock,
                            contentDescription = stringResource(id = R.string.lock_icon_content_desc),
                            passwordType = true
                        )
                        Text(
                            text = stringResource(id = R.string.create_account_profile_header),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(
                                top = 18.dp,
                                start = 22.dp,
                            )
                        )
                        LargeTextInputField(
                            text = firstNameState.value,
                            hint = stringResource(id = R.string.create_account_first_name_hint),
                            valueCallback = { firstNameState.value = it },
                            icon = Icons.Rounded.Person,
                            contentDescription = stringResource(id = R.string.person_icon_content_desc),
                            passwordType = false
                        )
                        LargeTextInputField(
                            text = lastNameState.value,
                            hint = stringResource(id = R.string.create_account_last_name_hint),
                            valueCallback = { lastNameState.value = it },
                            icon = Icons.Rounded.Group,
                            contentDescription = stringResource(id = R.string.group_icon_content_desc),
                            passwordType = false
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.create_account_choose_profile_header),
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier.padding(top = 28.dp, start = 22.dp)
                                )
                                Row(
                                    modifier = Modifier.padding(start = 20.dp, top = 5.dp)
                                ) {
                                    IconOnlyButton(
                                        icon = Icons.Rounded.Camera,
                                        contentDescription = stringResource(id = R.string.file_folder_content_desc),
                                        onClick = {
                                            createAccountActivity.openCameraForResult()
                                        },
                                    )
                                    IconOnlyButton(
                                        icon = Icons.Rounded.Folder,
                                        contentDescription = stringResource(id = R.string.camera_icon_content_desc),
                                        onClick = {
                                            createAccountActivity.openGalleryForResult()
                                        },
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(end = 22.dp, top = 22.dp)
                            ) {
                                Image(
                                    bitmap = avatarImageState.value.asImageBitmap(),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(100.dp)
                                        .aspectRatio(1F),
                                    contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                    alignment = Alignment.Center
                                )
                            }
                        }
                        Box {
                            BasicSnackbar(
                                hostState = scaffoldState.snackbarHostState,
                                modifier = Modifier.align(Alignment.BottomCenter),
                                icon = snackbarIconState.value,
                                contentDescription = snackbarIconDescription.value
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 25.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextOnlyButton(
                                text = stringResource(id = R.string.create_account_cancel_button),
                                color = colorResource(id = R.color.button_neutral_background_color),
                                onClick = {
                                    createAccountActivity.onBackPressed()
                                }
                            )
                            TextOnlyButton(
                                text = stringResource(id = R.string.create_account_continue_button),
                                color = colorResource(id = R.color.deep_sea),
                                onClick = {
                                    createAccountActivity.attemptCreateNewUser(
                                        emailState = emailState.value.text,
                                        passwordState = passwordState.value.text,
                                        nameState = firstNameState.value.text,
                                        lastNameState = lastNameState.value.text,
                                        snackbarState = scaffoldState.snackbarHostState,
                                        iconState = snackbarIconState,
                                        descriptionState = snackbarIconDescription
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}