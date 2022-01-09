package com.compose.app.android.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.utilities.getCloudPreferences
import com.compose.app.android.utilities.getDefaultPreferences
import kotlinx.coroutines.delay

@Composable
fun LaunchScreenView(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.4F))
        Image(
            painter = painterResource(id = R.drawable.ic_icon_transparent),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        LocalContext.current.let {
            LaunchedEffect(key1 = true) {
                delay(500L)
                val passwordAvailable =
                    it.getDefaultPreferences().getString("IDENTITY_USER_KEY", "") != "" &&
                            it.getCloudPreferences().getBoolean("STATE_APP_SECURED", false)
                navController.navigate(
                    if (FirebaseAccount().determineIfUserExists()) {
                        if (passwordAvailable) {
                            NavigationDestination.SecurityLockView
                        } else {
                            NavigationDestination.ProductivityView
                        }
                    } else {
                        NavigationDestination.WelcomeView
                    }
                )
            }
        }
    }
}
