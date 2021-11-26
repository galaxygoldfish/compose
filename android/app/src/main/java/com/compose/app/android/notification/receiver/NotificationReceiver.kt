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