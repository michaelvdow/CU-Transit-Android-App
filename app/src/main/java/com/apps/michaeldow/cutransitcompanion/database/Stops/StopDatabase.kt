package com.apps.michaeldow.cutransitcompanion.database.Stops

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [StopItem::class], version = 2, exportSchema = false)
abstract class StopDatabase : RoomDatabase() {

    abstract fun stopDao(): StopDao

    companion object {
        private const val DATABASE_NAME = "stops2"
        private const val DATABASE_PATH = "databases/stops.db"
        private var instance: StopDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): StopDatabase {
            if (instance == null) {
                instance = Room
                    .databaseBuilder(context, StopDatabase::class.java, DATABASE_NAME)
                    .createFromAsset(DATABASE_PATH)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}