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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.compose.app.android.firebase.FirebaseAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LogInViewModel : ViewModel() {

    val asyncScope = CoroutineScope(Dispatchers.IO + Job())

    /**
     * Attempt to sign in the user with their provided credentials, and
     * notify the user via snackbar if their credentials are invalid or if
     * there is no internet connection.
     * @param emailState - Value of the e-mail input field containing the
     * user's e-mail address.
     * @param passwordState - Value of the password text field containing
     * the user's password string.
     * @param context - While it is considered bad practice to contain a
     * reference of a context in a ViewModel, the function will only be
     * called when the user taps the sign-in button, when the app is likely
     * to not receive any configuration changes, avoiding a memory leak.
     * @param onFailure - Function to be invoked when the log-in attempt
     * results in a failure or exception.
     * @param onSuccess - Function to be invoked when the user has been
     * successfully authenticated.
     * @param onPreLaunch - Function to be invoked just before attempting
     * sign-in to notify the user of it's progress.
     */
    fun attemptSignIn(
        emailState: TextFieldValue,
        passwordState: TextFieldValue,
        context: Context,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
        onPreLaunch: () -> Unit
    ) {
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val synchronousScope = CoroutineScope(Dispatchers.Main + Job())
        asyncScope.launch {
            onPreLaunch.invoke()
            if (
                FirebaseAccount().authenticateWithEmail(
                    emailState.text,
                    passwordState.text,
                    context
                )
            ) {
                synchronousScope.launch {
                    onSuccess.invoke()
                }
            } else {
                synchronousScope.launch {
                    onFailure.invoke()
                }
            }
        }
    }

}