package com.apps.michaeldow.cutransitcompanion.views.trip_planner

import com.apps.michaeldow.cutransitcompanion.API.BaseRepository
import com.apps.michaeldow.cutransitcompanion.API.MtdApi
import com.apps.michaeldow.cutransitcompanion.API.responses.TripResponse.TripResponse
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import java.lang.Exception

class TripPlannerDatabaseProvider(private val api: MtdApi): BaseRepository() {

    suspend fun search(start: CarmenFeature, end: CarmenFeature): TripResponse? {
        val startLat = start?.center()?.latitude()
        val startLon = start?.center()?.longitude()
        val endLat = end?.center()?.latitude()
        val endLon = end?.center()?.longitude()

        if (startLat != null && startLon != null && endLat != null && endLon != null) {
            try {
                return safeApiCall(
                    call = {api.getPlannedTrips(startLat, startLon, endLat, endLon).await()},
                    errorMessage = "WHOOPS"
                )
            } catch (e: Exception) {
                return null
            }
        } else {
            return null
        }
    }


}