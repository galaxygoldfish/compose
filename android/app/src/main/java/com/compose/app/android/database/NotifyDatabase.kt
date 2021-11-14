package com.compose.app.android.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NotifyDatabaseEntity::class], version = 1)
abstract class NotifyDatabase : RoomDatabase() {

    abstract fun getDao() : NotifyDatabaseDAO

}