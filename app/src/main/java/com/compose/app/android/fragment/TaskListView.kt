package com.compose.app.android.fragment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaskListView() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Tasks")
    }
}