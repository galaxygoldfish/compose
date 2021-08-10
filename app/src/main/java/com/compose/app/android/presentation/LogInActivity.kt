package com.compose.app.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.compose.app.android.view.LogInView
import com.compose.app.android.viewmodel.LogInViewModel

class LogInActivity : ComponentActivity() {

    private val viewModel: LogInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogInView(context = this, viewModel)
        }
    }
}

