package com.apps.michaedow.cutransit.database.Stops

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stops")
data class StopItem(@PrimaryKey @NonNull @ColumnInfo(name = "_id") val id: Int,
                    @ColumnInfo(name = "stop_id") val stopId: String,
                    @ColumnInfo(name = "stop_code") val stopCode: String,
                    @ColumnInfo(name = "stop_name") val stopName: String,
                    @ColumnInfo(name = "corner_stop_name") val cornerStopName: String,
                    @ColumnInfo(name = "stop_lat") val stopLat: String,
                    @ColumnInfo(name = "stop_lon") val stopLon: String) {

}