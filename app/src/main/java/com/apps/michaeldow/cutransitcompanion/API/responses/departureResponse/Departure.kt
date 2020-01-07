package com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse

import java.io.Serializable

data class Departure (
    var stop_id: String,
    var headsign: String,
    var route: Route,
    var trip: Trip,
    var vehicle_id: String,
    var is_istop: Boolean,
    var expected: String,
    var expected_mins: Int
) : Serializable