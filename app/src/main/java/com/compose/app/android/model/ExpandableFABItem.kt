package com.compose.app.android.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ExpandableFABItem(
    val icon: ImageVector,
    val contentDescription: String,
    val label: String,
    val onClick: () -> Unit
)
