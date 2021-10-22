package com.compose.app.android.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.app.android.R

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
                elevation = 0.dp,
                backgroundColor = colorResource(id = R.color.neutral_gray),
                content = @Composable {
                    Row {
                        Icon(
                            painter = icon,
                            contentDescription = contentDescription,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            tint = colorResource(id = R.color.text_color_enabled)
                        )
                        Text(
                            text = data.message,
                            style = MaterialTheme.typography.body1,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 15.dp),
                            color = colorResource(id = R.color.text_color_enabled)
                        )
                    }
                },
            )
        },
    )
}

@Composable
fun SheetHandle() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(top = 15.dp)
                .width(100.dp)
                .height(4.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = colorResource(id = R.color.bottom_sheet_handle_color),
            elevation = 0.dp
        ) {  }
    }
}

@Composable
fun OptionListItem(
    icon: Int,
    contentDescription: String,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                onClick.invoke()
            }
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically)
        )
    }
}