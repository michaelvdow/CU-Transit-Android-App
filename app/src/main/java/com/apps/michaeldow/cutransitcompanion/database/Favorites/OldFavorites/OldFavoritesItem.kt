package com.apps.michaeldow.cutransitcompanion.database.Favorites.OldFavorites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteItem")
class OldFavoritesItem {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo(name = "stop_id")
    var stopId: String? = null
    @ColumnInfo(name = "stop_code")
    var stopCode: String? = null
    @ColumnInfo(name = "stop_name")
    var stopName: String? = null
    @ColumnInfo(name = "stop_desc")
    var stopDesc: String? = null
    @ColumnInfo(name = "stop_lat")
    var stopLat: String? = null
    @ColumnInfo(name = "stop_lon")
    var stopLon: String? = null
    @ColumnInfo(name = "zone_id")
    var zoneId: String? = null
    @ColumnInfo(name = "stop_url")
    var stopUrl: String? = null
    @ColumnInfo(name = "location_type")
    var locationType: String? = null
    @ColumnInfo(name = "parent_station")
    var parentStation: String? = null
    @ColumnInfo(name = "stop_timezone")
    var stopTimezone: String? = null
    @ColumnInfo(name = "wheelchair_boarding")
    var wheelchairBoarding: String? = null

}
