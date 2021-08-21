package com.compose.app.android.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BasicSnackbar(
    hostState: SnackbarHostState,
    modifier: Modifier,
    icon: Painter,
    contentDescription: String
) {
    SnackbarHost(
        modifier = modifier,
        hostState = hostState,
        snackbar = @Composable { data ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                content = @Composable {
                    Row {
                        Icon(
                            painter = icon,
                            contentDescription = contentDescription,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Text(
                            text = data.message,
                            style = MaterialTheme.typography.body1,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                },
            )
        },
    )
}