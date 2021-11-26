package com.compose.app.android.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.firebase.FirebaseUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    val avatarImageStore: MutableState<Bitmap?> = mutableStateOf(null)
    val userStorageUsage = mutableStateOf(0)

    private val asyncScope = CoroutineScope(Dispatchers.IO + Job())

    /**
     * Fetch the latest version of the user's avatar image from
     * Firebase and set the local image to the newest one.
     * @param filesDir - App-specific file directory path (context.filesDir)
     */
    fun updateToNewestAvatar(filesDir: String) {
        asyncScope.launch {
            FirebaseAccount().sendProfileImageToFile(filesDir)
            avatarImageStore.value = BitmapFactory.decodeFile("${filesDir}/avatar.png")
        }
    }

    fun updateUserStorageUsage() {
        asyncScope.launch {
            userStorageUsage.value = FirebaseUtils.calculateUserStorage()
        }
    }

}