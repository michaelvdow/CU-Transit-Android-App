package com.apps.michaeldow.cutransitcompanion.route.list

import com.apps.michaeldow.cutransitcompanion.API.BaseRepository
import com.apps.michaeldow.cutransitcompanion.API.MtdApi
import com.apps.michaeldow.cutransitcompanion.API.responses.stopTimesResponse.StopTime
import java.lang.Exception

class RouteListDatabaseProvider(val api: MtdApi): BaseRepository() {

    suspend fun getStops(tripId: String): List<StopTime>? {
        try {
            val response = safeApiCall(
                call = { api.getStopTimesByTrip(tripId).await() },
                errorMessage = "WHOOPS"
            )
            return response?.stop_times
        } catch (e: Exception) {
            return null
        }
    }

}