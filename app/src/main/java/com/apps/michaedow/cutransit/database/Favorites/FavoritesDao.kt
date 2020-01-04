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

    @Query("SELECT * FROM favorites ORDER BY rank ASC")
    fun getFavorites(): LiveData<List<FavoritesItem>>

    @Query("UPDATE favorites SET rank = :rank WHERE stop_id = :stopId")
    fun updateFavorite(stopId: String, rank: Int)

    @Query("SELECT count(*) FROM favorites WHERE stop_id = :stopId")
    fun containsStop(stopId: String?): Int

    @Query("SELECT MAX(rank) from favorites")
    fun getLastRank(): Int

}