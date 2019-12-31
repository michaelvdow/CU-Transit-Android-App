package com.apps.michaedow.cutransit.departures

import com.apps.michaedow.cutransit.API.BaseRepository
import com.apps.michaedow.cutransit.API.Departure
import com.apps.michaedow.cutransit.API.DeparturesResponse
import com.apps.michaedow.cutransit.API.MtdApi
import com.apps.michaedow.cutransit.database.Stops.StopDao

class DeparturesDatabaseProvider(private val api: MtdApi, private val dao: StopDao): BaseRepository() {

    suspend fun getDepartures(stopName: String): MutableList<Departure>? {
        var stopId = dao.getStop(stopName)?.get(0)?.stopId

        if (stopId != null) {
            // Remove text after colon to get all stops at intersection
            val index = stopId.indexOf(":")
            if (index != -1) {
                stopId = stopId.substring(0, index)
            }

            // Call query asynchronously
            val response = safeApiCall(
                call = {api.getDeparturesByStop(stopId, 30, 30).await()},
                errorMessage = "WHOOPS"
            )

            return response?.departures?.toMutableList()
        }
        return null
    }

}