package com.apps.michaedow.cutransit.database.Favorites

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoritesDao {

    @Insert
    fun insert(favoriteItem: FavoritesItem?): Long

    @Query("DELETE FROM favorites WHERE stop_name = :stopName")
    fun delete(stopName: String?)

    @Query("SELECT * FROM favorites")
    fun getFavorites(): LiveData<List<FavoritesItem>>

    @Query("SELECT count(*) FROM favorites WHERE stop_name = :stopName")
    fun containsStop(stopName: String?): Int

}