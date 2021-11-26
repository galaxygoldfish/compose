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
