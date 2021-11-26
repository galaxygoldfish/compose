/**
 * Copyright (C) 2021  Sebastian Hriscu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **/
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
