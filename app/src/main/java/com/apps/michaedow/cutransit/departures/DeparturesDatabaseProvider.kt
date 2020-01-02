package com.apps.michaedow.cutransit.departures

import com.apps.michaedow.cutransit.API.BaseRepository
import com.apps.michaedow.cutransit.API.Departure
import com.apps.michaedow.cutransit.API.DeparturesResponse
import com.apps.michaedow.cutransit.API.MtdApi
import com.apps.michaedow.cutransit.database.Favorites.FavoritesDao
import com.apps.michaedow.cutransit.database.Favorites.FavoritesItem
import com.apps.michaedow.cutransit.database.Stops.StopDao
import java.lang.Exception

class DeparturesDatabaseProvider(private val api: MtdApi, private val stopDao: StopDao, private val favoritesDao: FavoritesDao): BaseRepository() {

    suspend fun getDepartures(stopName: String): MutableList<Departure>? {
        var stopId = stopDao.getStop(stopName)?.get(0)?.stopId

        if (stopId != null) {
            // Remove text after colon to get all stops at intersection
            val index = stopId.indexOf(":")
            if (index != -1) {
                stopId = stopId.substring(0, index)
            }

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
        return null
    }

    // Returns whether it is now a favorite stop
    suspend fun updateFavorite(stopName: String): Boolean {
        if (favoritesDao.containsStop(stopName) > 0) {
            favoritesDao.delete(stopName)
            return false
        } else {
            val newItem = FavoritesItem(stopName)
            favoritesDao.insert(newItem)
            return true
        }
    }

    suspend fun isFavorite(stopName: String): Boolean {
        return favoritesDao.containsStop(stopName) > 0
    }

}