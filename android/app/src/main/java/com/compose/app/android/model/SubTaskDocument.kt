package com.compose.app.android.model

/**
 * A model representation of a sub-task item,
 * used in the task editor.
 *
 * @param taskName - The title of the sub-task
 * @param taskComplete - Whether the sub-task has
 * been checked off yet
 */
data class SubTaskDocument(
    var taskName: String?,
    var taskComplete: Boolean
)
