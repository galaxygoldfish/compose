package com.compose.app.android.model

data class TaskDocument(
    var taskID: String,
    var taskTitle: String,
    var taskContent: String,
    var dueDate: String,
    var dueTime: String,
    var isComplete: Boolean,
    var taskDueEpoch: Double
)
