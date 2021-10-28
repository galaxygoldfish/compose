package com.compose.app.android.model

/**
 * Model representation of a task document in Firebase
 * @param taskID - The firebase file name or ID
 * @param taskTitle - The user's title for the task
 * @param taskContent - Task details and content
 * @param dueDate - The task's due date (human readable)
 * @param dueTime - The time that the task is due (human readable)
 * @param taskDueEpoch - Unix timestamp of the task's due date
 * @param isComplete - Whether the task has been checked off yet
 */
data class TaskDocument(
    var taskID: String,
    var taskTitle: String,
    var taskContent: String,
    var dueDate: String,
    var dueTime: String,
    var isComplete: Boolean,
    var taskDueEpoch: Double
)
