package com.compose.app.android.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.compose.app.android.notification.service.NotificationIntentService
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context!!.apply {
            val intentService = Intent(this, NotificationIntentService::class.java).apply {
                putExtra("NOTIFICATION_EXTRA_ID", intent!!.getStringExtra("SERVICE_EXTRA_ID"))
                putExtra("NOTIFICATION_EXTRA_TITLE", intent.getStringExtra("SERVICE_EXTRA_TITLE"))
                putExtra(
                    "NOTIFICATION_EXTRA_CONTENT",
                    intent.getStringExtra("SERVICE_EXTRA_CONTENT")
                )
            }
            startService(intentService)
        }
    }

}