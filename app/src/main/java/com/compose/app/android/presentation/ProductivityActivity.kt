package com.compose.app.android.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.view.ProductivityView
import com.compose.app.android.viewmodel.ProductivityViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

class ProductivityActivity : ComponentActivity() {

    private val viewModel: ProductivityViewModel by viewModels()

    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!FirebaseAccount().determineIfUserExists()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        } else {
            viewModel.synchronousScope.launch {
                viewModel.updateToNewestAvatar(filesDir.path)
                setContent {
                    ProductivityView(context = this@ProductivityActivity, viewModel = viewModel)
                }
            }
        }
    }
}
