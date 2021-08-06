package com.compose.app.android.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.compose.app.android.R

val InterTypeface = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
    Font(R.font.inter_semibold, FontWeight.W600)
)

val Typography = Typography(

    h1 = TextStyle(
        fontFamily = InterTypeface,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp
    ),

    body1 = TextStyle(
        fontFamily = InterTypeface,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    body2 = TextStyle(
        fontFamily = InterTypeface,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp
    ),

    button = TextStyle(
        fontFamily = InterTypeface,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
    ),

    overline = TextStyle(
        fontFamily = InterTypeface,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),

    caption = TextStyle(
        fontFamily = InterTypeface,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )

)