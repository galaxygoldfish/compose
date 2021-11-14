package com.compose.app.android.notification.service

import android.app.IntentService
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.compose.app.android.R
import com.compose.app.android.notification.TaskNotificationManager
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
class NotificationIntentService : IntentService("Task Notifications") {

    override fun onHandleIntent(intent: Intent?) {
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