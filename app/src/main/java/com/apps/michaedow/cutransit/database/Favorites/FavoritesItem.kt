package com.apps.michaedow.cutransit.database.Favorites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesItem(@ColumnInfo(name = "stop_name") val stopName: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}