package com.apps.michaedow.cutransit.route.map

import com.apps.michaedow.cutransit.API.BaseRepository
import com.apps.michaedow.cutransit.API.MtdApi
import com.apps.michaedow.cutransit.API.responses.shapeResponse.Shape
import com.apps.michaedow.cutransit.API.responses.stopTimesResponse.StopTime
import java.lang.Exception

class RouteMapDatabaseProvider(val api: MtdApi): BaseRepository() {

    suspend fun getShapes(shapeId: String): List<Shape>? {
        try {
            val response = safeApiCall(
                call = { api.getShape(shapeId).await() },
                errorMessage = "WHOOPS"
            )
            return response?.shapes
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getStopTimes(tripId: String): List<StopTime>? {
        try {
            val response = safeApiCall(
                call = { api.getStopTiemsByTrip(tripId).await() },
                errorMessage = "WHOOPS"
            )
            return response?.stop_times
        } catch (e: Exception) {
            return null
        }
    }


}