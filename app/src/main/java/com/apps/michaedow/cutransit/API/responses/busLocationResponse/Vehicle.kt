package com.apps.michaedow.cutransit.API.responses.busLocationResponse

import com.apps.michaedow.cutransit.API.responses.departureResponse.Trip

data class Vehicle (
    var vehicle_id: String,
    var trip: Trip,
    var location: BusLocation,
    var previous_stop_id: String,
    var next_stop_id: String,
    var origin_stop_id: String,
    var destination_stop_id: String,
    var last_updated: String
)