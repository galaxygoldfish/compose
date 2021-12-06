package com.compose.app.android.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.compose.app.android.utilities.getDefaultPreferences

class SecurityLockViewModel : ViewModel() {

    val passwordEnteredText = mutableStateOf(TextFieldValue(""))

    fun authenticate(context: Context) : Boolean {
        val password = context.getDefaultPreferences().getString("IDENTITY_USER_KEY", "")
        return passwordEnteredText.value.text == password
    }

}