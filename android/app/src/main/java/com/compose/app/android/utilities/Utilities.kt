package com.compose.app.android.utilities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.viewmodel.CreateAccountViewModel
import com.compose.app.android.viewmodel.SettingsViewModel

fun handleProfileImageResult(
    data: Intent?,
    requestCode: Int,
    context: ComposeBaseActivity,
    avatarImageLive: MutableState<Bitmap?>
) {
    data?.let {
        fun camera() {
            it.extras?.let { extras ->
                avatarImageLive.value = extras.get("data") as Bitmap
            }
        }
        fun gallery() {
            avatarImageLive.value = MediaStore.Images.Media.getBitmap(
                context.contentResolver, it.data
            )
        }
        when (requestCode) {
            CreateAccountViewModel.CreateAccountRequestCode.CODE_INTENT_CAMERA -> camera()
            SettingsViewModel.SettingsRequestCode.CAMERA_REQUEST -> camera()
            CreateAccountViewModel.CreateAccountRequestCode.CODE_INTENT_GALLERY -> gallery()
            SettingsViewModel.SettingsRequestCode.GALLERY_REQUEST -> gallery()
            else -> Log.e("COMPOSE", "Utilities#handleProfileImageResult [ERROR: CODE]")
        }
    }
}

/**
 * Faster way to get a viewModel class instance from the
 * ViewModelProvider, eliminating boilerplate code.
 *
 * @param type - The ViewModel type to retrieve, for example,
 * ProductivityViewModel::class.java may be passed in to get
 * the current instance of that viewModel
 * @return - An instance of the viewModel type specified, or
 * if it does not exist in the provider, a new one will be
 * created and returned
 */
fun <T : ViewModel> Context.getViewModel(type: Class<T>) : T = ViewModelProvider(this as ComposeBaseActivity).get(type)