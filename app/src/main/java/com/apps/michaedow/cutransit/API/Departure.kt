package com.apps.michaedow.cutransit.API

data class Departure (
    var stop_id: String,
    var headsign: String,
    var route: Route,
    var trip: Trip,
    var vehicle_id: String,
    var expected: String,
    var expected_mins: String
)