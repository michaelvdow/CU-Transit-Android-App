package com.apps.michaedow.cutransit.route.list

import com.apps.michaedow.cutransit.API.BaseRepository
import com.apps.michaedow.cutransit.API.MtdApi
import com.apps.michaedow.cutransit.API.responses.stopTimesResponse.StopTime
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