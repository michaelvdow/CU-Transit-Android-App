package com.apps.michaeldow.cutransitcompanion.database.Stops

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query

@Dao
interface StopDao {

    @Query("SELECT * FROM stops WHERE stop_name LIKE (:stopName || '%' ) LIMIT 1")
    fun getStop(stopName: String?): List<StopItem?>?

    @Query("SELECT * FROM stops")
    fun getAllStops(): List<StopItem>

    @Query("SELECT DISTINCT * FROM stops GROUP BY stop_name ORDER BY ABS(:lat - stop_lat)*ABS(:lat - stop_lat) + ABS(:lon - stop_lon)*ABS(:lon - stop_lon) ASC LIMIT 25")
    suspend fun getClosestStops(
        lat: Double,
        lon: Double
    ): List<StopItem>

    @Query("WITH t AS (SELECT * FROM stops GROUP BY stop_name) SELECT * FROM t WHERE t.stop_name LIKE '%' || :stopName || '%' LIMIT 50")
    fun getCursor(stopName: String?): Cursor?

    @Query("SELECT DISTINCT * FROM stops GROUP BY stop_name")
    fun getBusStops(): List<StopItem>

    @Query("SELECT stop_name FROM stops WHERE stop_id LIKE (:stopId || ':%') LIMIT 1")
    fun getStopNameById(stopId: String): String?

}