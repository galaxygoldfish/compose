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
package com.compose.app.android.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.theme.DeepSeaAccent
import com.compose.app.android.theme.currentAppAccentColor
import com.compose.app.android.theme.currentAppThemeState
import com.compose.app.android.utilities.getCloudPreferences
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.view.*
import com.compose.app.android.view.settings.*
import com.compose.app.android.viewmodel.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object NavigationDestination {
    const val WelcomeView = "welcome"
    const val LogInView = "login"
    const val CreateAccountView = "createAccount"
    const val ProductivityView = "productivity"
    const val NoteEditorView = "noteEditor"
    const val TaskEditorView = "taskEditor"
    const val SecurityLockView = "security"
    const val SettingsViewHome = "settings"
    const val CustomizationSettings = "customizationSettings"
    const val AccountSettings = "accountSettings"
    const val SecurityPrivacySettings = "securityPrivacySettings"
    const val NotificationSettings = "notificationSettings"
    const val AboutAppSettings = "aboutAppSettings"
    const val AccessibilitySettings = "accessibilitySettings"
    const val HelpFeedbackSettings = "helpFeedbackSettings"
    const val CreateFeedbackView = "createFeedback"
}

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalPagerApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalGraphicsApi::class
)
class ComposeBaseActivity : ComponentActivity() {

    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private val logInViewModel: LogInViewModel by viewModels()
    private val productivityViewModel: ProductivityViewModel by viewModels()
    private val noteEditorViewModel: NoteEditorViewModel by viewModels()
    private val taskEditorViewModel: TaskEditorViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val securityViewModel: SecurityLockViewModel by viewModels()

    private lateinit var navigationController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Firebase.auth.currentUser != null) {

            val preferences = getCloudPreferences()
            preferences.synchronizeLocal()

            FirebaseAccount().updateLocalMetadata(this)

            currentAppThemeState.value = preferences.getBoolean("STATE_DARK_MODE", false)
            currentAppAccentColor.value = Color(preferences.getInteger("STATE_ACCENT_COLOR", DeepSeaAccent.toArgb()))

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
        }

        setContent {
            ComposeTheme {
                ComposeNavigationHost(intent)
            }
        }
    }

    @Composable
    fun ComposeNavigationHost(intent: Intent) {

        navigationController = rememberAnimatedNavController()

        val passwordAvailable = LocalContext.current.let {
            it.getDefaultPreferences().getString("IDENTITY_USER_KEY", "") != "" &&
                    it.getCloudPreferences().getBoolean("STATE_APP_SECURED", false)
        }

        val navigationStart = if (FirebaseAccount().determineIfUserExists()) {
            if (passwordAvailable) {
                NavigationDestination.SecurityLockView
            } else {
                NavigationDestination.ProductivityView
            }
        } else {
            NavigationDestination.WelcomeView
        }

        val animatedEnterZoom: (
            AnimatedContentScope<String>.(NavBackStackEntry, NavBackStackEntry) -> EnterTransition
        ) = { _: NavBackStackEntry, _: NavBackStackEntry ->
            expandIn(
                expandFrom = Alignment.TopCenter,
                animationSpec = tween(200)
            ) + fadeIn(
                animationSpec = tween(300)
            )
        }
        val animatedExitSlide: (
            AnimatedContentScope<String>.(NavBackStackEntry, NavBackStackEntry) -> ExitTransition
        ) = { _: NavBackStackEntry, _: NavBackStackEntry ->
            slideOutOfContainer(
                towards = AnimatedContentScope.SlideDirection.Down,
                animationSpec = tween(300)
            ) + fadeOut(
                animationSpec = tween(300)
            )
        }
        val animatedSlideRight: (
            AnimatedContentScope<String>.(NavBackStackEntry, NavBackStackEntry) -> EnterTransition
        ) = { _: NavBackStackEntry, _: NavBackStackEntry ->
            slideIntoContainer(
                towards = AnimatedContentScope.SlideDirection.Right
            )
        }
        val animatedSlideLeft: (
            AnimatedContentScope<String>.(NavBackStackEntry, NavBackStackEntry) -> ExitTransition
        ) = { _: NavBackStackEntry, _: NavBackStackEntry ->
            slideOutOfContainer(
                towards = AnimatedContentScope.SlideDirection.Left
            )
        }

        AnimatedNavHost(
            navController = navigationController,
            startDestination = navigationStart,
            builder = {
                composable(NavigationDestination.WelcomeView) {
                    keyboardPop(false)
                    WelcomeView(
                        context = this@ComposeBaseActivity,
                        navController = navigationController
                    )
                }
                composable(NavigationDestination.CreateAccountView) {
                    keyboardPop(true)
                    CreateAccountView(
                        context = this@ComposeBaseActivity,
                        viewModel = createAccountViewModel,
                        navController = navigationController
                    )
                }
                composable(NavigationDestination.LogInView) {
                    keyboardPop(true)
                    LogInView(
                        context = this@ComposeBaseActivity,
                        viewModel = logInViewModel,
                        navController = navigationController
                    )
                }
                composable(NavigationDestination.ProductivityView) {
                    keyboardPop(false)
                    ProductivityView(
                        context = this@ComposeBaseActivity,
                        viewModel = productivityViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = """${NavigationDestination.NoteEditorView}/{noteID}""",
                    exitTransition = animatedExitSlide,
                    enterTransition = animatedEnterZoom
                ) { backStackEntry ->
                    keyboardPop(true)
                    NoteEditorView(
                        viewModel = noteEditorViewModel,
                        navController = navigationController,
                        documentID = backStackEntry.arguments!!.getString("noteID")!!
                    )
                }
                composable(
                    route = """${NavigationDestination.TaskEditorView}/{taskID}""",
                    exitTransition = animatedExitSlide,
                    enterTransition = animatedEnterZoom
                ) { backStackEntry ->
                    keyboardPop(true)
                    TaskEditorView(
                        navController = navigationController,
                        documentID = backStackEntry.arguments!!.getString("taskID")!!,
                        viewModel = taskEditorViewModel
                    )
                }
                composable(
                    route = NavigationDestination.SecurityLockView
                ) {
                    SecurityLockView(
                        viewModel = securityViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.SettingsViewHome,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    SettingsHomePage(
                        viewModel = settingsViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.CustomizationSettings,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    UICustomizationSettings(
                        viewModel = settingsViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.AccountSettings,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    AccountSettings(
                        viewModel = settingsViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.SecurityPrivacySettings,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    SecurityPrivacySettings(
                        viewModel = settingsViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.NotificationSettings,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    NotificationSettings(
                        viewModel = settingsViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.AboutAppSettings,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    AboutAppSettings(
                        viewModel = settingsViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.AccessibilitySettings,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    AccessibilitySettings(
                        viewModel = settingsViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.HelpFeedbackSettings,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    HelpFeedbackSettings(
                        viewModel = settingsViewModel,
                        navController = navigationController
                    )
                }
                composable(
                    route = NavigationDestination.CreateFeedbackView,
                    enterTransition = animatedSlideRight,
                    exitTransition = animatedSlideLeft
                ) {
                    CreateFeedbackView(
                        navController = navigationController,
                        viewModel = settingsViewModel
                    )
                }
            }
        )
        // If coming from notification tap action
        intent.getStringExtra("TASK_ID_NOTIFICATION")?.let { idExtra ->
            navigationController.navigate("""${NavigationDestination.TaskEditorView}/$idExtra""")
        }
        // If coming from settings - dark theme switch
        // TODO - Make separate destination that doesn't include fade animation
        //  to avoid faded parts of ProductivityView from appearing
        intent.extras?.get("SETTINGS_THEME_APPLY")?.let {
            navigationController.navigate(NavigationDestination.CustomizationSettings)
        }
    }

    /**
     * Set flag to determine whether to resize the view layout
     * dynamically with the keyboard or to ignore it.
     *
     * @param enable - Set to true if layout should resize with
     * the keyboard or IME
     */
    private fun keyboardPop(enable: Boolean) {
        window.setSoftInputMode(
            when (enable) {
                true -> SOFT_INPUT_ADJUST_RESIZE
                false -> SOFT_INPUT_ADJUST_NOTHING
            }
        )
    }

    /**
     * Check if the request is coming from a profile change
     * action, and if so, send data to the according viewModel.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when {
                // CreateAccountView is the requester
                (requestCode == 1 || requestCode == 2) -> {
                    createAccountViewModel.processActivityResult(data, requestCode, this)
                }
                // SettingsView->AccountSettingsView is the requester
                (requestCode == 3 || requestCode == 4) -> {
                    settingsViewModel.onActivityResult(data, requestCode, this)
                }
            }
        }
    }

    /**
     * When base context is attached, configuration changes can
     * be overridden, so the font scale chosen in accessibility
     * settings can be applied
     */
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)

        val newFontScale = newBase?.getDefaultPreferences()
           ?.getString("STATE_FONT_SIZE", "1.0")?.toFloat()

        Configuration().apply {
            newFontScale?.let { fontScale = it }
            applyOverrideConfiguration(this)
        }

    }

    override fun onBackPressed() {
        NavigationDestination.apply {
            navigationController.currentDestination?.route?.let {
                when {
                    it.contains(NoteEditorView) -> noteEditorViewModel.saveNoteContents()
                    it.contains(TaskEditorView) -> taskEditorViewModel.saveTaskData(this@ComposeBaseActivity)
                }
            }
        }
        super.onBackPressed()
    }

}