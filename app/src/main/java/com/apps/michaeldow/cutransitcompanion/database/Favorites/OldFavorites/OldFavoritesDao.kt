package com.apps.michaeldow.cutransitcompanion.database.Favorites.OldFavorites

import androidx.room.Dao
import androidx.room.Query

@Dao
interface OldFavoritesDao {

    @Query("SELECT * FROM FavoriteItem")
    fun getFavorites(): List<OldFavoritesItem>

}