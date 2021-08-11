package com.compose.app.android.presentation

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.utilities.rawStringResource
import com.compose.app.android.view.CreateAccountView
import com.compose.app.android.viewmodel.CreateAccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CreateAccountActivity : ComponentActivity() {

    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateAccountView(context = this, viewModel = viewModel)
        }
    }

    /**
     * Attempt to create a new user in Firebase, and if there is a failure, notify
     * the user via a snackbar.
     * @param emailState - The user's e-mail address to be used when creating the account
     * @param passwordState - The user's preferred password to be used when creating
     * the account
     * @param nameState - The user's first name
     * @param lastNameState - The user's last name
     * @param snackbarState - The SnackbarHostState needed from the view to show an
     * error snackbar
     * @param iconState - The snackbar icon state to be used when changing between
     * success and error snackbars
     * @param descriptionState - The snackbar's contentDescription state to be used
     * when switching between snackbar icons.
     */
    fun attemptCreateNewUser(
        emailState: String,
        passwordState: String,
        nameState: String,
        lastNameState: String,
        snackbarState: SnackbarHostState,
        iconState: MutableState<ImageVector>,
        descriptionState: MutableState<String>
    ) {
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        asyncScope.launch {
            iconState.value = Icons.Rounded.AccountTree
            descriptionState.value = rawStringResource(R.string.account_tree_icon_content_desc)
            snackbarState.showSnackbar(message = rawStringResource(R.string.create_account_queue_text))
            val avatar: Bitmap = if (viewModel.avatarImageLive.value == null) {
                BitmapFactory.decodeResource(resources, R.drawable.default_avatar_image)
            } else {
                viewModel.avatarImageLive.value!!
            }
            val accountResult = FirebaseAccount().createNewAccount(
                emailState, passwordState, nameState,
                lastNameState, avatar, this@CreateAccountActivity
            )
            if (accountResult == "true") {
                startActivity(Intent(this@CreateAccountActivity, ProductivityActivity::class.java))
            } else {
                iconState.value = Icons.Rounded.Warning
                descriptionState.value = rawStringResource(R.string.warning_icon_content_desc)
                snackbarState.showSnackbar(accountResult)
            }
        }
    }

    /**
     * Opens the device's default gallery app expecting an image
     * as a result.
     */
    fun openGalleryForResult() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, CreateAccountViewModel.CODE_INTENT_GALLERY)
    }

    /**
     * Opens the device's default camera app expecting an image
     * in return.
     */
    fun openCameraForResult() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(pictureIntent, CreateAccountViewModel.CODE_INTENT_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                when (requestCode) {
                    CreateAccountViewModel.CODE_INTENT_CAMERA -> {
                        it.extras?.let { extras ->
                            viewModel.avatarImageLive.value = extras.get("data") as Bitmap
                        }
                    }
                    CreateAccountViewModel.CODE_INTENT_GALLERY -> {
                        viewModel.avatarImageLive.value =
                            MediaStore.Images.Media.getBitmap(contentResolver, it.data)
                    }
                    else -> Log.e("COMPOSE", "Unexpected exception occurred")
                }
            }
        }
    }
}