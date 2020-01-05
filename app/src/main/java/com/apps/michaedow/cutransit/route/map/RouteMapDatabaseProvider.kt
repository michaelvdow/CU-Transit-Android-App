package com.apps.michaedow.cutransit.route.map

import com.apps.michaedow.cutransit.API.BaseRepository
import com.apps.michaedow.cutransit.API.MtdApi
import com.apps.michaedow.cutransit.API.responses.busLocationResponse.Vehicle
import com.apps.michaedow.cutransit.API.responses.shapeResponse.Shape
import com.apps.michaedow.cutransit.API.responses.stopTimesResponse.StopTime
import com.google.android.gms.maps.model.LatLng
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