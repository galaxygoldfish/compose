package com.compose.app.android.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

val currentAppThemeState = mutableStateOf(false)

@Composable
fun ComposeTheme(
    darkTheme: Boolean = currentAppThemeState.value,
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) {
       getDarkColorPalette()
    } else {
        getLightColorPalette()
    }

    MaterialTheme(
        colors = colors,
        typography = typography(),
        shapes = Shapes,
        content = content
    )

}