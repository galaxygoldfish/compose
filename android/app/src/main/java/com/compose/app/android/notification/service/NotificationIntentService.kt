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
package com.compose.app.android.notification.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.compose.app.android.R
import com.compose.app.android.notification.TaskNotificationManager

class NotificationIntentService : IntentService("Task Notifications") {

    override fun onHandleIntent(intent: Intent?) {
        Log.e("COMPOSE", "NotificationIntentService#onHandleIntent")
        val context = this
        intent?.let { it ->
            val title = it.getStringExtra("NOTIFICATION_EXTRA_TITLE")!!
            val content = it.getStringExtra("NOTIFICATION_EXTRA_CONTENT")!!
            val taskID = it.getStringExtra("NOTIFICATION_EXTRA_ID")!!
            TaskNotificationManager.apply {
                createNotificationChannel(
                    description = context.getString(R.string.notification_service_channel_desc),
                    name = context.getString(R.string.notification_service_channel_title),
                    context = context
                )
                sendTaskNotification(
                    context = context,
                    title = title,
                    content = content,
                    taskID = taskID
                )
            }
        }
    }

}