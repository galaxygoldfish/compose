package com.compose.app.android.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.IconAlert
import com.compose.app.android.theme.IconPersonSingle
import com.compose.app.android.utilities.rawStringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
class CreateAccountViewModel : ViewModel() {

    companion object {
        const val CODE_INTENT_GALLERY = 1
        const val CODE_INTENT_CAMERA = 2
    }

    var avatarImageLive: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    val avatarRowPadding = mutableStateOf(25.dp)

    /**
     * Opens the device's default gallery app expecting an image
     * as a result.
     */
    @ExperimentalAnimationApi
    fun openGalleryForResult(context: ComposeBaseActivity) {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        context.startActivityForResult(galleryIntent, CODE_INTENT_GALLERY)
    }

    /**
     * Opens the device's default camera app expecting an image
     * in return.
     */
    @ExperimentalAnimationApi
    fun openCameraForResult(context: ComposeBaseActivity) {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context.startActivityForResult(pictureIntent, CODE_INTENT_CAMERA)
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
     * TODO -> Add missing params
     */
    fun attemptCreateNewUser(
        emailState: String,
        passwordState: String,
        nameState: String,
        lastNameState: String,
        snackbarState: SnackbarHostState,
        iconState: MutableState<Int>,
        descriptionState: MutableState<String>,
        context: Context,
        navController: NavController
    ) {
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val synchronousScope = CoroutineScope(Dispatchers.Main + Job())
        asyncScope.launch {
            iconState.value = IconPersonSingle
            descriptionState.value = context.rawStringResource(R.string.account_tree_icon_content_desc)
            avatarRowPadding.value = 5.dp
            snackbarState.showSnackbar(message = context.rawStringResource(R.string.create_account_queue_text))
            val avatar: Bitmap = if (avatarImageLive.value == null) {
                BitmapFactory.decodeResource(context.resources, R.drawable.default_avatar_image)
            } else {
                avatarImageLive.value!!
            }
            val accountResult = FirebaseAccount().createNewAccount(
                emailState, passwordState, nameState,
                lastNameState, avatar, context
            )
            if (accountResult == "true") {
                synchronousScope.launch {
                    navController.navigate(NavigationDestination.ProductivityActivity)
                }
            } else {
                iconState.value = IconAlert
                descriptionState.value = context.rawStringResource(R.string.warning_icon_content_desc)
                snackbarState.showSnackbar(accountResult, duration = SnackbarDuration.Indefinite)
            }
        }
    }

    /**
     * Process the result of the gallery or camera opening to
     * capture an avatar image.
     *
     * @param data - The Intent object returned from onActivityResult
     * @param requestCode - The code returned from the Intent used to
     * determine the nature of it
     * @param context - A context needed to parse the Bitmap returned
     * from the gallery
     */
    @ExperimentalAnimationApi
    fun processActivityResult(data: Intent?, requestCode: Int, context: ComposeBaseActivity) {
        data?.let {
            when (requestCode) {
                CODE_INTENT_CAMERA -> {
                    it.extras?.let { extras ->
                        avatarImageLive.value = extras.get("data") as Bitmap
                    }
                }
                CODE_INTENT_GALLERY -> {
                    avatarImageLive.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it.data)
                }
                else -> Log.e("COMPOSE", "Unexpected intent : CreateAccountViewModel.kt > processActivityResult()")
            }
        }
    }

}