package com.apps.michaeldow.cutransitcompanion.views.main_activity.favorites

import androidx.lifecycle.LiveData
import com.apps.michaeldow.cutransitcompanion.Utils.Utils
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesDao
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesItem
import com.apps.michaeldow.cutransitcompanion.database.Favorites.OldFavorites.OldFavoritesDao
import com.apps.michaeldow.cutransitcompanion.database.Stops.StopDao

class FavoritesDatabaseProvider(private val dao: FavoritesDao) {

    fun getFavorites(): LiveData<List<FavoritesItem>> {
        return dao.getFavorites()
    }

    fun updateFavorite(stopId: String, rank: Int) {
        dao.updateFavorite(stopId, rank)
    }

    fun getOldFavorites(oldFavoritesDao: OldFavoritesDao, stopDao: StopDao) {
        val oldFavorites = oldFavoritesDao.getFavorites()
        for (favorite in oldFavorites) {
            if (dao.containsStopByName(favorite.stopName) == 0) {
                val stopItems = stopDao.getStop(favorite.stopName)
                val stopItem = stopItems?.get(0)
                if (stopItem != null) {
                    val stopId = Utils.fixStopId(stopItem.stopId)
                    val newRank = dao.getLastRank()
                    val newFavorite = FavoritesItem(stopItem.stopName, stopId, newRank)
                    dao.insert(newFavorite)
                }
            }

        }
    }

}