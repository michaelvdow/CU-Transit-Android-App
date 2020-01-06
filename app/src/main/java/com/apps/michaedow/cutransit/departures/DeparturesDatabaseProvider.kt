package com.apps.michaedow.cutransit.departures

import com.apps.michaedow.cutransit.API.BaseRepository
import com.apps.michaedow.cutransit.API.responses.departureResponse.Departure
import com.apps.michaedow.cutransit.API.MtdApi
import com.apps.michaedow.cutransit.database.Favorites.FavoritesDao
import com.apps.michaedow.cutransit.database.Favorites.FavoritesItem
import com.apps.michaedow.cutransit.database.Stops.StopDao
import java.lang.Exception

class DeparturesDatabaseProvider(private val api: MtdApi, private val stopDao: StopDao, private val favoritesDao: FavoritesDao): BaseRepository() {

    fun getStopName(stopId: String): String? {
        return stopDao.getStopNameById(stopId)
    }

    suspend fun getDepartures(stopId: String): MutableList<Departure>? {
        // Call query asynchronously
        try {
            val response = safeApiCall(
                call = { api.getDeparturesByStop(stopId, 30, 30).await() },
                errorMessage = "WHOOPS"
            )
            return response?.departures?.toMutableList()
        } catch (e: Exception) {
            return null
        }
    }

    // Returns whether it is now a favorite stop
    fun updateFavorite(stopName: String, stopId: String): Boolean {
        if (favoritesDao.containsStop(stopId) > 0) {
            favoritesDao.delete(stopId)
            return false
        } else {
            val lastRank = favoritesDao.getLastRank()
            val newItem = FavoritesItem(stopName, stopId, lastRank)
            favoritesDao.insert(newItem)
            return true
        }
    }

    fun isFavorite(stopId: String): Boolean {
        return favoritesDao.containsStop(stopId) > 0
    }

}