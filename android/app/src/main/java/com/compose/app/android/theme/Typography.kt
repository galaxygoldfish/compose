package com.compose.app.android.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
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
    Font(R.font.inter_semibold, FontWeight.SemiBold)
)

@Composable
fun typography() : Typography{
    return Typography(
        h1 = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = MaterialTheme.colors.onBackground
        ),
        h2 = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            color = MaterialTheme.colors.onBackground
        ),
        h4 = TextStyle(
            fontFamily = InterTypeface,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground
        ),
        h6 = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 19.sp,
            color = MaterialTheme.colors.onBackground
        ),
        body1 = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground
        ),
        body2 = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground
        ),
        subtitle1 = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground.copy(0.7F)
        ),
        subtitle2 = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground
        ),
        button = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground
        ),
        overline = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground
        ),
        caption = TextStyle(
            fontFamily = InterTypeface,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = MaterialTheme.colors.onBackground
        )
    )
}