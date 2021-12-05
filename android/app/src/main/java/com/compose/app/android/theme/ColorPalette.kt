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
package com.compose.app.android.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getDarkColorPalette(): Colors {
    return darkColors(
        primary = currentAppAccentColor.value,
        primaryVariant = NeutralColorDark,
        secondary = currentAppAccentColor.value,
        secondaryVariant = NeutralButtonColorDark,
        background = Color.Black,
        surface = Color.Black,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White
    )
}

@Composable
fun getLightColorPalette(): Colors {
    return lightColors(
        primary = currentAppAccentColor.value,
        primaryVariant = NeutralColorLight,
        secondary = currentAppAccentColor.value,
        secondaryVariant = NeutralButtonColorLight,
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
    )
}

val DeepSeaAccent = Color(0xFF3B7EF1)

val NeutralButtonColorLight = Color(0XFFEDEDED)
val NeutralButtonColorDark = Color(0X54C3C3C3)
val NeutralColorLight = Color(0XFFEDEDED)
val NeutralColorDark = Color(0XFF202020)

val TextColorRed = Color(0XFFF06292)
val TextColorOrange = Color(0XFFFFB74D)
val TextColorYellow = Color(0XFFFFF176)
val TextColorGreen = Color(0XFF81C784)
val TextColorBlue = Color(0XFF64B5F6)
val TextColorPurple = Color(0XFF9575CD)

