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
package com.compose.app.android.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.compose.app.android.R
import com.compose.app.android.database.NotifyRepository
import com.compose.app.android.notification.receiver.NotificationReceiver
import com.compose.app.android.presentation.ComposeBaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class TaskNotificationManager {

    companion object {

        private const val TASK_CHANNEL_ID = "NotificationService"

        /**
         * Schedule a notification for a task to be delivered at the
         * specified time. If the device is shut down before it can be
         * delivered, the notification is stored in the database and will
         * be rescheduled upon reboot.
         *
         * @param context - A context, needed to access the system service
         * @param taskID - The ID of the task to be used when tapping on the
         * notification, to open the corresponding task
         * @param title - The title of the notification
         * @param content - The body text of the notification
         * @param timeUnix - A long holding the corresponding unix time to
         * deliver the notification at
         */
        fun scheduleTaskNotification(
            context: Context,
            taskID: String,
            title: String,
            content: String,
            timeUnix: Long
        ) {
            Log.e("COMPOSE", "TaskNotificationManager#scheduleTaskNotification")
            val notifyIntent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("SERVICE_EXTRA_ID", taskID)
                putExtra("SERVICE_EXTRA_TITLE", title)
                putExtra("SERVICE_EXTRA_CONTENT", content)
            }
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getBroadcast(context, Random.nextInt(), notifyIntent, FLAG_IMMUTABLE)
            } else {
                getBroadcast(context, Random.nextInt(), notifyIntent, FLAG_UPDATE_CURRENT)
            }
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).apply {
                set(AlarmManager.RTC_WAKEUP, timeUnix, pendingIntent)
            }
            CoroutineScope(Dispatchers.IO).launch {
                NotifyRepository.insertNotificationToDatabase(
                    notificationTitle = title,
                    notificationBody = content,
                    notifyTimeUnix = timeUnix,
                    targetTaskID = taskID,
                    context = context
                )
            }
        }

        /**
         * Send a notification to the user
         *
         * @param context - A context needed to access the notification
         * service
         * @param title - The title of the notification to be delivered
         * @param content - The body text of the notification to be
         * delivered
         * @param taskID - The ID of the task, to be used when clicking on
         * the notification to open the task editor
         */
        fun sendTaskNotification(
            context: Context,
            title: String,
            content: String,
            taskID: String
        ) {
            Log.e("COMPOSE", "TaskNotificationManager#sendTaskNotification")
            val intent = Intent(context, ComposeBaseActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("TASK_ID_NOTIFICATION", taskID)
            }
            val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getActivity(context, 0, intent, FLAG_IMMUTABLE)
            } else {
                getActivity(context, 0, intent, 0)
            }
            val builder = NotificationCompat.Builder(context, TASK_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_round_add_task_24)
                .setLargeIcon(
                    BitmapFactory.decodeResource(context.resources, R.drawable.ic_round_add_task_24)
                )
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
            with(NotificationManagerCompat.from(context)) {
                notify(Random.nextInt(), builder.build())
            }
        }

        /**
         * Create a notification channel to pass notifications through.
         * This method can be called multiple times without effect since
         * the system detects whether the same channel has already been created
         *
         * @param description - A description of the notification channel
         * to be shown to the user in app settings
         * @param name - The name of the channel to also be displayed in the
         * notification settings
         * @param context - A context needed to access the system service
         */
        fun createNotificationChannel(description: String, name: String, context: Context) {
            Log.e("COMPOSE", "TaskNotificationManager#createNotificationChannel")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(TASK_CHANNEL_ID, name, importance).apply {
                    this.description = description
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

    }
}