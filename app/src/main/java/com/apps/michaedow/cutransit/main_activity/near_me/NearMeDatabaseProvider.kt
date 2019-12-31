package com.apps.michaedow.cutransit.main_activity.near_me

import android.location.Location
import com.apps.michaedow.cutransit.database.Stops.StopDao
import com.apps.michaedow.cutransit.database.Stops.StopItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NearMeDatabaseProvider(private val dao: StopDao) {

    suspend fun getNearbyStops(location: Location) : List<StopItem> {
        return withContext(Dispatchers.IO) {
            dao.getClosestStops(location.latitude, location.longitude)
        }
    }

}