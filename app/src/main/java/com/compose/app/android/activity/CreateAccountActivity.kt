package com.compose.app.android.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.compose.app.android.R
import com.compose.app.android.account.FirebaseAccount
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.utilities.rawStringResource
import com.compose.app.android.view.BasicSnackbar
import com.compose.app.android.view.IconOnlyButton
import com.compose.app.android.view.LargeTextInputField
import com.compose.app.android.view.TextOnlyButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CreateAccountActivity : ComponentActivity() {

    companion object {
        const val CODE_INTENT_GALLERY = 1
        const val CODE_INTENT_CAMERA = 2
    }

    private var avatarImageLive: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }

    @Composable
    fun MainContent() {

        val emailState = remember { mutableStateOf(TextFieldValue()) }
        val passwordState = remember { mutableStateOf(TextFieldValue()) }

        val firstNameState = remember { mutableStateOf(TextFieldValue()) }
        val lastNameState = remember { mutableStateOf(TextFieldValue()) }

        val scaffoldState = rememberScaffoldState()
        val snackbarIconState = remember { mutableStateOf(Icons.Rounded.Warning) }
        val snackbarIconDescription = remember { mutableStateOf(rawStringResource(R.string.warning_icon_content_desc)) }
        val avatarImageState = remember { mutableStateOf(BitmapFactory.decodeResource(resources, R.drawable.default_avatar_image)) }

        val avatarImageUpdater = Observer<Bitmap> { avatar ->
            avatarImageState.value = avatar
        }

        avatarImageLive.observe(this, avatarImageUpdater)

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
                                    startActivity(
                                        Intent(
                                            this@CreateAccountActivity,
                                            WelcomeActivity::class.java
                                        )
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
                                                openCameraForResult()
                                            },
                                        )
                                        IconOnlyButton(
                                            icon = Icons.Rounded.Folder,
                                            contentDescription = stringResource(id = R.string.camera_icon_content_desc),
                                            onClick = {
                                                openGalleryForResult()
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
                                        onBackPressed()
                                    }
                                )
                                TextOnlyButton(
                                    text = stringResource(id = R.string.create_account_continue_button),
                                    color = colorResource(id = R.color.deep_sea),
                                    onClick = {
                                        attemptCreateNewUser(
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

    private fun attemptCreateNewUser(emailState: String, passwordState: String, nameState: String, lastNameState: String, snackbarState: SnackbarHostState,
                                     iconState: MutableState<ImageVector>, descriptionState: MutableState<String>) {
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        asyncScope.launch {
            iconState.value = Icons.Rounded.AccountTree
            descriptionState.value = rawStringResource(R.string.account_tree_icon_content_desc)
            snackbarState.showSnackbar(message = rawStringResource(R.string.create_account_queue_text))
            val avatar: Bitmap = if (avatarImageLive.value == null) {
                BitmapFactory.decodeResource(resources, R.drawable.default_avatar_image)
            } else {
                avatarImageLive.value!!
            }
            val accountResult = FirebaseAccount().createNewAccount(emailState, passwordState, nameState,
            lastNameState, avatar, this@CreateAccountActivity)
            if (accountResult == "true") {
                startActivity(Intent(this@CreateAccountActivity, ProductivityActivity::class.java))
            } else {
                iconState.value = Icons.Rounded.Warning
                descriptionState.value = rawStringResource(R.string.warning_icon_content_desc)
                snackbarState.showSnackbar(accountResult)
            }
        }
    }

    private fun openGalleryForResult() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, CODE_INTENT_GALLERY)
    }

    private fun openCameraForResult() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(pictureIntent, CODE_INTENT_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                when (requestCode) {
                    CODE_INTENT_CAMERA -> {
                        it.extras?.let { extras ->
                            avatarImageLive.value = extras.get("data") as Bitmap
                        }
                    }
                    CODE_INTENT_GALLERY -> {
                        avatarImageLive.value = MediaStore.Images.Media.getBitmap(contentResolver, it.data)
                    }
                    else -> Log.e("COMPOSE", "Unexpected exception occurred")
                }
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun CreateAccountPreview() {
    CreateAccountActivity().MainContent()
}