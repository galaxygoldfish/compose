package com.compose.app.android.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.compose.app.android.R

@Composable
fun FullWidthButton(text: String, icon: ImageVector, contentDescription: String,
                    color: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .size(width = 1000.dp, height = 60.dp)
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        elevation = elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.Black
            )
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 1.dp, start = 24.dp),
            )
        }
    }
}

@Composable
fun TextOnlyButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp)
            .size(height = 45.dp, width = Dp.Unspecified),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        elevation = elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box {
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun IconOnlyButton(icon: ImageVector, onClick: () -> Unit, contentDescription: String) {
    Button(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp, end = 15.dp)
            .size(height = 45.dp, width = Dp.Unspecified),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.button_neutral_background_color)
        ),
        elevation = elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}