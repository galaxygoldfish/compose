package com.compose.app.android.model

import androidx.compose.ui.graphics.painter.Painter

data class ExpandableFAB(
    val icon: Painter,
    val contentDescription: String,
    val label: String,
    val onClick: () -> Unit
)

enum class ExpandableFABState {
    COLLAPSED,
    EXPANDED
}
