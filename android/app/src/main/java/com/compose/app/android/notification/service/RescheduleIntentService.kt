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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.compose.app.android.database.NotifyRepository
import com.compose.app.android.notification.TaskNotificationManager
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RescheduleIntentService : IntentService("Compose Notification Rescheduler") {

    override fun onHandleIntent(intent: Intent?) {
        val context = this
        CoroutineScope(Dispatchers.IO).launch {
            val notificationList = NotifyRepository.getStoredNotifications(context)
            notificationList.forEach { entity ->
                TaskNotificationManager.scheduleTaskNotification(
                    context = context,
                    taskID = entity.targetTaskID,
                    title = entity.notificationTitle,
                    content = entity.notificationDescription,
                    timeUnix = entity.notifyTimeUnix
                )
            }
        }
    }

}