package com.apps.michaedow.cutransit.database.Stops

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StopDao {

    @Query("SELECT * FROM stops WHERE stop_name LIKE :stopName LIMIT 1")
    fun getStop(stopName: String?): List<StopItem?>?

    @Query("SELECT * FROM stops")
    fun getAllStops(): List<StopItem>

//    TODO: FIX THIS AWFUL QUERY FROM OLD APPLICATION
    @Query("WITH t AS (SELECT * FROM stops GROUP BY stop_name) SELECT * FROM t WHERE t.stop_name IN (:stops) GROUP BY stop_name ORDER BY CASE WHEN stop_name LIKE :stop1 THEN 1 WHEN stop_name LIKE :stop2 THEN 2 WHEN stop_name LIKE :stop3 THEN 3 WHEN stop_name LIKE :stop4 THEN 4 WHEN stop_name LIKE :stop5 THEN 5 WHEN stop_name LIKE :stop6 THEN 6 WHEN stop_name LIKE :stop7 THEN 7 WHEN stop_name LIKE :stop8 THEN 8 WHEN stop_name LIKE :stop9 THEN 9 WHEN stop_name LIKE :stop10 THEN 10 END")
    fun getExact(
        stops: List<String?>?,
        stop1: String?,
        stop2: String?,
        stop3: String?,
        stop4: String?,
        stop5: String?,
        stop6: String?,
        stop7: String?,
        stop8: String?,
        stop9: String?,
        stop10: String?
    ): Cursor?

    @Query("SELECT DISTINCT * FROM stops GROUP BY stop_name ORDER BY ABS(:lat - stop_lat)*ABS(:lat - stop_lat) + ABS(:lon - stop_lon)*ABS(:lon - stop_lon) ASC LIMIT 25")
    suspend fun getClosestStops(
        lat: Double,
        lon: Double
    ): List<StopItem>

    @Query("WITH t AS (SELECT * FROM stops GROUP BY stop_name) SELECT * FROM t WHERE t.stop_name LIKE '%' || :stopName || '%' LIMIT 50")
    fun getCursor(stopName: String?): Cursor?

    @Query("SELECT DISTINCT * FROM stops GROUP BY stop_name")
    fun getBusStops(): List<StopItem>

}