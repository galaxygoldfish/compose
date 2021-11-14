package com.compose.app.android.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotifyDatabaseEntity(

    @PrimaryKey(autoGenerate = true)
    var itemID: Int? = null,

    var notificationTitle: String,

    var notificationDescription: String,

    var notifyTimeUnix: Long,

    var targetTaskID: String

)