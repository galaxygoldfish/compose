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
package com.compose.app.android.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.firebase.FirebaseUtils
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.utilities.handleProfileImageResult
import com.compose.app.android.viewmodel.SettingsViewModel.SettingsRequestCode.CAMERA_REQUEST
import com.compose.app.android.viewmodel.SettingsViewModel.SettingsRequestCode.GALLERY_REQUEST
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    object SettingsRequestCode {
        const val GALLERY_REQUEST = 3
        const val CAMERA_REQUEST = 4
    }

    val avatarImageStore: MutableState<Bitmap?> = mutableStateOf(null)
    val userStorageUsage = mutableStateOf(0)

    val showingLogOutDialog = mutableStateOf(false)
    val showingEditAccountDialog = mutableStateOf(false)
    val showingPasswordDialog = mutableStateOf(false)
    val showingColorPickerDialog = mutableStateOf(false)
    val showingAccountDeleteDialog = mutableStateOf(false)

    val tempAvatarImage = mutableStateOf(avatarImageStore.value)
    val tempFirstName = mutableStateOf(TextFieldValue(""))
    val tempLastName = mutableStateOf(TextFieldValue(""))
    val tempPassword = mutableStateOf(TextFieldValue(""))

    private val asyncScope = CoroutineScope(Dispatchers.IO + Job())

    init { updateUserStorageUsage() }

    /**
     * Fetch the latest version of the user's avatar image from
     * Firebase and set the local image to the newest one.
     * @param filesDir - App-specific file directory path (context.filesDir)
     */
    fun setAvatarImage(filesDir: String, skipCheck: Boolean? = false) {
        if (avatarImageStore.value == null || skipCheck == true) {
            asyncScope.launch {
                FirebaseAccount().sendProfileImageToFile(filesDir)
                BitmapFactory.decodeFile("${filesDir}/avatar.png").let {
                    avatarImageStore.value = it
                    tempAvatarImage.value = it
                }
            }
        }
    }

    fun updateUserNameDetails(context: Context) {
        context.getDefaultPreferences().apply {
            tempLastName.value = TextFieldValue(
                getString("IDENTITY_USER_NAME_FIRST", "Error")!!
            )
            tempFirstName.value = TextFieldValue(
                getString("IDENTITY_USER_NAME_LAST", "Error")!!
            )
            tempPassword.value = TextFieldValue(
                getString("IDENTITY_USER_AUTHENTICATOR", "Error")!!
            )
        }
    }

    fun updateUserStorageUsage() {
        asyncScope.launch {
            userStorageUsage.value = FirebaseUtils.calculateUserStorage()
        }
    }

    /**
     * Opens the device's default gallery app expecting an image
     * as a result.
     */
    fun openGalleryForResult(context: Context) {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        (context as ComposeBaseActivity).startActivityForResult(galleryIntent, GALLERY_REQUEST)
    }

    /**
     * Opens the device's default camera app expecting an image
     * in return.
     */
    fun openCameraForResult(context: Context) {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        (context as ComposeBaseActivity).startActivityForResult(pictureIntent, CAMERA_REQUEST)
    }

    /**
     * When the gallery or camera is closed, process the
     * activity result and save the image.
     */
    fun onActivityResult(data: Intent?, requestCode: Int, context: ComposeBaseActivity) {
        handleProfileImageResult(data, requestCode, context, tempAvatarImage)
    }

    /**
     * Finalize changes and
     */
    fun updateAccountEdit(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseAccount().apply {
                if ((tempAvatarImage.value?.equals(avatarImageStore.value)) == false) {
                    uploadNewProfileImage(
                        avatarImage = tempAvatarImage.value!!,
                        context = context
                    )
                }
                if (
                    tempFirstName.value.text.isNotEmpty() &&
                    tempLastName.value.text.isNotEmpty()
                ) {
                    Log.e("COMPOSE", "non empty")
                    uploadNewUserMetadata(
                        mapData = hashMapOf(
                            "FIRST-NAME" to tempFirstName.value.text,
                            "LAST-NAME" to tempLastName.value.text
                        )
                    ).run { Log.e("COMPOSE", this.toString()) }
                    updateLocalMetadata(context)
                }
            }
        }
    }

    fun getPasswordHidden(context: Context) : String {
        val password = context.getDefaultPreferences()
            .getString("IDENTITY_USER_AUTHENTICATOR", "Error")!!
        var tempDisplayText = ""
        repeat(password.length) { tempDisplayText += "*" }
        return tempDisplayText
    }

    fun updatePasswordEdit(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            Firebase.auth.currentUser?.let {
                context.getDefaultPreferences().apply {
                    it.reauthenticate(
                        EmailAuthProvider.getCredential(
                            it.email!!,
                            getString("IDENTITY_USER_AUTHENTICATOR", "")!!
                        )
                    )
                    it.updatePassword(tempPassword.value.text).addOnSuccessListener {
                        edit().putString("IDENTITY_USER_AUTHENTICATOR", tempPassword.value.text)
                            .commit()
                    }
                }
            }
        }
    }


}