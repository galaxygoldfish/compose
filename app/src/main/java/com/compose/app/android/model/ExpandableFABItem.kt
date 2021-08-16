package com.compose.app.android.model

import androidx.compose.ui.graphics.painter.Painter

data class ExpandableFABItem(
    val icon: Painter,
    val contentDescription: String,
    val label: String,
    val onClick: () -> Unit
)
