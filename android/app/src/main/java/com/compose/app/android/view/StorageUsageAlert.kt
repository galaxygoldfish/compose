package com.compose.app.android.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.DialogScaffold
import com.compose.app.android.components.FullWidthButton
import com.compose.app.android.components.colorCorrectedSecondary
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.IconBackArrow
import com.compose.app.android.theme.IconCheckMark
import com.compose.app.android.theme.IconStorage
import com.compose.app.android.theme.IconTrashItem

@ExperimentalAnimationApi
@Composable
fun StorageUsageAlertDialog(
    showState: MutableState<Boolean>,
    onDiscardAction: () -> Unit,
    navController: NavController
) {
    AnimatedVisibility(
        visible = showState.value
    ) {
        Dialog(
            onDismissRequest = {
                showState.value = false
            }
        ) {
            DialogScaffold(
                text = stringResource(id = R.string.storage_usage_alert_header),
                icon = painterResource(id = IconStorage)
            ) {
                Text(
                    text = stringResource(id = R.string.storage_usage_alert_body),
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 10.dp,
                        bottom = 15.dp
                    )
                )
                FullWidthButton(
                    text = stringResource(id = R.string.storage_usage_alert_positive_button),
                    icon = painterResource(id = IconCheckMark),
                    contentDescription = "",
                    color = colorCorrectedSecondary()
                ) {
                    showState.value = false
                }
                FullWidthButton(
                    text = stringResource(id = R.string.storage_usage_alert_neutral_button),
                    icon = painterResource(id = IconBackArrow),
                    contentDescription = "",
                    color = colorCorrectedSecondary()
                ) {
                    showState.value = false
                    navController.navigate(NavigationDestination.ProductivityView)
                }
                FullWidthButton(
                    text = stringResource(id = R.string.storage_usage_alert_negative_button),
                    icon = painterResource(id = IconTrashItem),
                    contentDescription = "",
                    color = colorCorrectedSecondary()
                ) {
                    onDiscardAction.invoke()
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
            }
        }
    }
}