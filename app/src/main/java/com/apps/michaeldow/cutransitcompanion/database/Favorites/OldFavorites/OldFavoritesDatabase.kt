package com.apps.michaeldow.cutransitcompanion.database.Favorites.OldFavorites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OldFavoritesItem::class], version = 1, exportSchema = false)
abstract class OldFavoritesDatabase : RoomDatabase() {

    abstract fun oldFavoritesDao(): OldFavoritesDao

    companion object {
        private const val DATABASE_NAME = "favorites.db"
        private var instance: OldFavoritesDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): OldFavoritesDatabase {
            if (instance == null) {
                instance = Room
                    .databaseBuilder(context, OldFavoritesDatabase::class.java, DATABASE_NAME)
                    .build()
            }

            return instance!!
        }

    }
}