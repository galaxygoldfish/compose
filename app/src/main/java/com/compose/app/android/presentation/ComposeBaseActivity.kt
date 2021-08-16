package com.compose.app.android.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.view.CreateAccountView
import com.compose.app.android.view.LogInView
import com.compose.app.android.view.NoteEditorView
import com.compose.app.android.view.ProductivityView
import com.compose.app.android.view.TaskEditorView
import com.compose.app.android.view.WelcomeView
import com.compose.app.android.viewmodel.CreateAccountViewModel
import com.compose.app.android.viewmodel.LogInViewModel
import com.compose.app.android.viewmodel.NoteEditorViewModel
import com.compose.app.android.viewmodel.ProductivityViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

object NavigationDestination {
    const val WelcomeActivity = "welcome"
    const val LogInActivity = "login"
    const val CreateAccountActivity = "createAccount"
    const val ProductivityActivity = "productivity"
    const val NoteEditorActivity = "noteEditor"
    const val TaskEditorActivity = "taskEditor"
}

@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
class ComposeBaseActivity : ComponentActivity() {

    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private val logInViewModel: LogInViewModel by viewModels()
    private val productivityViewModel: ProductivityViewModel by viewModels()
    private val noteEditorViewModel: NoteEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeNavigationHost()
        }
    }

    @Composable
    fun ComposeNavigationHost() {
        val navigationController = rememberNavController()
        val navigationStart = if (FirebaseAccount().determineIfUserExists()) {
            NavigationDestination.ProductivityActivity
        } else {
            NavigationDestination.WelcomeActivity
        }
        ComposeTheme {
            NavHost(
                navController = navigationController,
                startDestination = navigationStart,
                builder = {
                    composable(NavigationDestination.WelcomeActivity) {
                        WelcomeView(
                            context = this@ComposeBaseActivity,
                            navController = navigationController
                        )
                    }
                    composable(NavigationDestination.CreateAccountActivity) {
                        CreateAccountView(
                            context = this@ComposeBaseActivity,
                            viewModel = createAccountViewModel,
                            navController = navigationController
                        )
                    }
                    composable(NavigationDestination.LogInActivity) {
                        LogInView(
                            context = this@ComposeBaseActivity,
                            viewModel = logInViewModel,
                            navController = navigationController
                        )
                    }
                    composable(NavigationDestination.ProductivityActivity) {
                        ProductivityView(
                            context = this@ComposeBaseActivity,
                            viewModel = productivityViewModel,
                            navController = navigationController
                        )
                    }
                    composable("""${NavigationDestination.NoteEditorActivity}/{noteID}""") { backStackEntry ->
                        NoteEditorView(
                            viewModel = noteEditorViewModel,
                            context = this@ComposeBaseActivity,
                            navController = navigationController,
                            documentID = backStackEntry.arguments!!.getString("noteID")!!
                        )
                    }
                    composable(NavigationDestination.TaskEditorActivity) {
                        TaskEditorView(
                            navController = navigationController
                        )
                    }
                }
            )
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            createAccountViewModel.processActivityResult(data, requestCode, this)
        }
    }

}