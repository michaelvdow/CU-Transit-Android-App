package com.apps.michaedow.cutransit.API.responses.stopTimesResponse

data class StopTime (
    var arrival_time: String,
    var departure_time: String,
    var stop_sequence: String,
    var stop_point: StopPoint
)