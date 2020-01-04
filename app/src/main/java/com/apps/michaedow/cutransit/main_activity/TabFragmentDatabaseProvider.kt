package com.apps.michaedow.cutransit.main_activity

import com.apps.michaedow.cutransit.API.AutocompleteApi.AutocompleteApi
import com.apps.michaedow.cutransit.API.AutocompleteApi.Suggestion
import com.apps.michaedow.cutransit.API.BaseRepository
import com.apps.michaedow.cutransit.database.Stops.StopDao
import java.lang.Exception

class TabFragmentDatabaseProvider(private val api: AutocompleteApi, private val stopDao: StopDao): BaseRepository() {

    suspend fun search(query: String): List<Suggestion>? {
        try {
            return safeApiCall(
                call = {api.getAutocomplete(query).await()},
                errorMessage = "WHOOPS"
            )
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getStopId(stopName: String): String? {
        val stops = stopDao.getStop(stopName)
        if (stops != null) {
            return stops[0]?.stopId
        }
        return null
    }

}