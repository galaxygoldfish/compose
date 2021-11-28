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

    val showingLogOutDialog = mutableStateOf(false)

    private val asyncScope = CoroutineScope(Dispatchers.IO + Job())

    init { updateUserStorageUsage() }

    /**
     * Fetch the latest version of the user's avatar image from
     * Firebase and set the local image to the newest one.
     * @param filesDir - App-specific file directory path (context.filesDir)
     */
    fun setAvatarImage(filesDir: String) {
        if (avatarImageStore.value == null) {
            asyncScope.launch {
                FirebaseAccount().sendProfileImageToFile(filesDir)
                avatarImageStore.value = BitmapFactory.decodeFile("${filesDir}/avatar.png")
            }
        }
    }

    fun updateUserStorageUsage() {
        asyncScope.launch {
            userStorageUsage.value = FirebaseUtils.calculateUserStorage()
        }
    }

}