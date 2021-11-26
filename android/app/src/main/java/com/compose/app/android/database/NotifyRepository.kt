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
package com.compose.app.android.database

import android.content.Context
import androidx.room.Room

class NotifyRepository {

    companion object {

        private const val DATABASE_NAME = "notification-database"

        /**
         * Save a notification to the database so that it can
         * be restored after boot if needed
         *
         * @param notificationTitle - The title of the notification
         * to be delivered
         * @param notificationBody - The body text of the notification
         * to be delivered
         * @param notifyTimeUnix - A long representing the unix time
         * value of the notification's delivery time
         * @param context - A context needed to access the database
         */
        suspend fun insertNotificationToDatabase(
            notificationTitle: String,
            notificationBody: String,
            notifyTimeUnix: Long,
            targetTaskID: String,
            context: Context
        ) {
            val database = gainDatabaseAccess(context)
            val notificationEntity = NotifyDatabaseEntity(
                notificationTitle = notificationTitle,
                notificationDescription = notificationBody,
                targetTaskID = targetTaskID,
                notifyTimeUnix = notifyTimeUnix
            )
            database.addNotification(notification = notificationEntity)
        }

        /**
         * Retrieve all saved notifications that were scheduled before
         * the device was rebooted
         *
         * @param context - A context needed to access the database
         * @return - A list of NotifyDatabaseEntity containing details
         * for each scheduled notification
         */
        suspend fun getStoredNotifications(context: Context): List<NotifyDatabaseEntity> {
            val database = gainDatabaseAccess(context)
            return database.getAllStoredNotifications()
        }

        /**
         * Get access to an instance of the notification
         * database's DAO
         *
         * @param context - A context used to build the database instance
         */
        private fun gainDatabaseAccess(context: Context): NotifyDatabaseDAO {
            return Room.databaseBuilder(
                context, NotifyDatabase::class.java, DATABASE_NAME
            ).build().getDao()
        }

    }

}