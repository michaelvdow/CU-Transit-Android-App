package com.apps.michaeldow.cutransitcompanion.route.map

import com.apps.michaeldow.cutransitcompanion.API.BaseRepository
import com.apps.michaeldow.cutransitcompanion.API.MtdApi
import com.apps.michaeldow.cutransitcompanion.API.responses.busLocationResponse.Vehicle
import com.apps.michaeldow.cutransitcompanion.API.responses.shapeResponse.Shape
import com.apps.michaeldow.cutransitcompanion.API.responses.stopTimesResponse.StopTime
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
                call = { api.getStopTimesByTrip(tripId).await() },
                errorMessage = "WHOOPS"
            )
            return response?.stop_times
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getBusLocation(vehicleId: String): Vehicle? {
        try {
            val response = safeApiCall(
                call = { api.getBusLocation(vehicleId).await() },
                errorMessage = "WHOOPS"
            )
            return response?.vehicles?.get(0)
        } catch (e: Exception) {

        }
        return null
    }


}