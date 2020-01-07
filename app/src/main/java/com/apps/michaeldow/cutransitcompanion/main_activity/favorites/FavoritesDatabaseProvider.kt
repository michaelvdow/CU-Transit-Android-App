package com.apps.michaeldow.cutransitcompanion.main_activity.favorites

import androidx.lifecycle.LiveData
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesDao
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesItem

class FavoritesDatabaseProvider(private val dao: FavoritesDao) {

    fun getFavorites(): LiveData<List<FavoritesItem>> {
        return dao.getFavorites()
    }

    fun updateFavorite(stopId: String, rank: Int) {
        dao.updateFavorite(stopId, rank)
    }

}