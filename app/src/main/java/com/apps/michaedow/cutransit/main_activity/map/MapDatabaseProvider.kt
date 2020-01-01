package com.apps.michaedow.cutransit.main_activity.map

import com.apps.michaedow.cutransit.database.Stops.StopDao
import com.apps.michaedow.cutransit.database.Stops.StopItem

class MapDatabaseProvider(private val dao: StopDao) {

    suspend fun getStops(): List<StopItem> {
        return dao.getBusStops()
    }

}