package com.apps.michaeldow.cutransitcompanion.database.Favorites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoritesItem::class], version = 1, exportSchema = false)
abstract class FavoritesDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
    companion object {
        private const val DATABASE_NAME = "favorites2"
        private var instance: FavoritesDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): FavoritesDatabase {
            if (instance == null) {
                instance = Room
                    .databaseBuilder(context, FavoritesDatabase::class.java, DATABASE_NAME)
                    .build()
            }

            return instance!!
        }

    }
}