package com.compose.app.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.compose.app.android.R

@Composable
fun ComposeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
       darkColors(
            primary = colorResource(id = R.color.deep_sea),
            primaryVariant = colorResource(id = R.color.button_neutral_background_color),
            secondary = colorResource(id = R.color.deep_sea),
            background = colorResource(id = R.color.background_color),
            surface = colorResource(id = R.color.background_color),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColors(
            primary = colorResource(id = R.color.deep_sea),
            primaryVariant = colorResource(id = R.color.button_neutral_background_color),
            secondary = colorResource(id = R.color.deep_sea),
            background = colorResource(id = R.color.background_color),
            surface = Color.White,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black,
        )
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}