package com.apps.michaeldow.cutransitcompanion.API.responses.stopTimesResponse

data class StopPoint (
    var code: String,
    var stop_id: String,
    var stop_lat: Double,
    var stop_lon: Double,
    var stop_name: String
)