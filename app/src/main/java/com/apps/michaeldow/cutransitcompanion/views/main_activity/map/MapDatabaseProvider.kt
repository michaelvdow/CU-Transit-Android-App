package com.apps.michaeldow.cutransitcompanion.views.main_activity.map

import com.apps.michaeldow.cutransitcompanion.database.Stops.StopDao
import com.apps.michaeldow.cutransitcompanion.database.Stops.StopItem

class MapDatabaseProvider(private val dao: StopDao) {

    fun getStops(): List<StopItem> {
        return dao.getBusStops()
    }

}