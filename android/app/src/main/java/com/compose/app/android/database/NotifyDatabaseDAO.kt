package com.compose.app.android.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotifyDatabaseDAO {

    @Query("SELECT * FROM notifications")
    fun getAllStoredNotifications(): List<NotifyDatabaseEntity>

    @Query("DELETE FROM notifications WHERE itemID = :notificationID")
    fun deleteNotificationByID(notificationID: Int)

    @Insert
    fun addNotification(notification: NotifyDatabaseEntity)

}