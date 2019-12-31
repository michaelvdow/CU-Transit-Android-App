package com.apps.michaedow.cutransit.database.Stops

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StopItem::class], version = 1, exportSchema = false)
abstract class StopDatabase : RoomDatabase() {

    abstract fun stopDao(): StopDao

    companion object {
        private const val DATABASE_NAME = "stops"
        private const val DATABASE_PATH = "databases/stops.db"
        private var instance: StopDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): StopDatabase {
            if (instance == null) {
                instance = Room
                    .databaseBuilder(context, StopDatabase::class.java, DATABASE_NAME)
                    .createFromAsset(DATABASE_PATH)
                    .build()
            }
            return instance!!
        }

    }
}