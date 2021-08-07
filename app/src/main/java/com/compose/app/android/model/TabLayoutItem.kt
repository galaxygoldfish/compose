package com.compose.app.android.model

import androidx.compose.runtime.Composable

data class TabLayoutItem(
    var tabTitle: String,
    val tabContent: @Composable () -> Unit
)