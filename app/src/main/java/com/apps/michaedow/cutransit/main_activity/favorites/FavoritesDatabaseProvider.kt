package com.apps.michaedow.cutransit.main_activity.favorites

import androidx.lifecycle.LiveData
import com.apps.michaedow.cutransit.database.Favorites.FavoritesDao
import com.apps.michaedow.cutransit.database.Favorites.FavoritesItem

class FavoritesDatabaseProvider(private val dao: FavoritesDao) {

    fun getFavorites(): LiveData<List<FavoritesItem>> {
        return dao.getFavorites()
    }

}