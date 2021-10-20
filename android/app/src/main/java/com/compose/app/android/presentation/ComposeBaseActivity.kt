package com.compose.app.android.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.view.*
import com.compose.app.android.viewmodel.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
class ComposeBaseActivity : ComponentActivity() {

    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private val logInViewModel: LogInViewModel by viewModels()
    private val productivityViewModel: ProductivityViewModel by viewModels()
    private val noteEditorViewModel: NoteEditorViewModel by viewModels()
    private val taskEditorViewModel: TaskEditorViewModel by viewModels()

    private lateinit var navigationController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeNavigationHost()
        }
    }

    @Composable
    fun ComposeNavigationHost() {
        navigationController = rememberAnimatedNavController()
        val navigationStart = if (FirebaseAccount().determineIfUserExists()) {
            NavigationDestination.ProductivityActivity
        } else {
            NavigationDestination.WelcomeActivity
        }
        ComposeTheme {
            AnimatedNavHost(
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
                    composable("""${NavigationDestination.TaskEditorActivity}/{taskID}""") { backStackEntry ->
                        TaskEditorView(
                            navController = navigationController,
                            documentID = backStackEntry.arguments!!.getString("taskID")!!,
                            viewModel = taskEditorViewModel
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