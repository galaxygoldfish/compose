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

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
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