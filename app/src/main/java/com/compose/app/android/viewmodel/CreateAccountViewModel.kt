package com.compose.app.android.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateAccountViewModel : ViewModel() {

    companion object {
        const val CODE_INTENT_GALLERY = 1
        const val CODE_INTENT_CAMERA = 2
    }

    var avatarImageLive: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()

}