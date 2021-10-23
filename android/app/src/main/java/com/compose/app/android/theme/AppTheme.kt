package com.compose.app.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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