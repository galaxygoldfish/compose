package com.compose.app.android.presentation

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.theme.currentAppThemeState
import com.compose.app.android.utilities.getDefaultPreferences
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

        val preferences = getDefaultPreferences()
        currentAppThemeState.value = preferences.getBoolean("STATE_DARK_MODE", false)

        setTheme(
            if (currentAppThemeState.value) {
                window.statusBarColor = resources.getColor(R.color.black)
                R.style.Theme_Compose_Dark
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                R.style.Theme_Compose_Light
            }
        )

        setContent {
            ComposeTheme {
                ComposeNavigationHost(intent)
            }
        }
    }

    @Composable
    fun ComposeNavigationHost(intent: Intent) {

        navigationController = rememberAnimatedNavController()
        val navigationStart = if (FirebaseAccount().determineIfUserExists()) {
            NavigationDestination.ProductivityActivity
        } else {
            NavigationDestination.WelcomeActivity
        }

        val animatedEnter: (
        AnimatedContentScope<String>.(NavBackStackEntry, NavBackStackEntry) -> EnterTransition
        ) = { _: NavBackStackEntry, _: NavBackStackEntry ->
            expandIn(
                expandFrom = Alignment.TopCenter,
                animationSpec = tween(200)
            ) + fadeIn(
                animationSpec = tween(300)
            )
        }
        val animatedExit: (
        AnimatedContentScope<String>.(NavBackStackEntry, NavBackStackEntry) -> ExitTransition
        ) = { _: NavBackStackEntry, _: NavBackStackEntry ->
            slideOutOfContainer(
                towards = AnimatedContentScope.SlideDirection.Down,
                animationSpec = tween(300)
            ) + fadeOut(
                animationSpec = tween(300)
            )
        }

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
                composable(
                    route = """${NavigationDestination.NoteEditorActivity}/{noteID}""",
                    exitTransition = animatedExit,
                    enterTransition = animatedEnter
                ) { backStackEntry ->
                    NoteEditorView(
                        viewModel = noteEditorViewModel,
                        navController = navigationController,
                        documentID = backStackEntry.arguments!!.getString("noteID")!!
                    )
                }
                composable(
                    route = """${NavigationDestination.TaskEditorActivity}/{taskID}""",
                    exitTransition = animatedExit,
                    enterTransition = animatedEnter
                ) { backStackEntry ->
                    TaskEditorView(
                        navController = navigationController,
                        documentID = backStackEntry.arguments!!.getString("taskID")!!,
                        viewModel = taskEditorViewModel
                    )
                }
            }
        )
        intent.getStringExtra("TASK_ID_NOTIFICATION")?.let { idExtra ->
            navigationController.navigate("""${NavigationDestination.TaskEditorActivity}/$idExtra""")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            createAccountViewModel.processActivityResult(data, requestCode, this)
        }
    }

}