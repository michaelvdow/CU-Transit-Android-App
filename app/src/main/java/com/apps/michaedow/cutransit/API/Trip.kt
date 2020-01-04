package com.apps.michaedow.cutransit.API

import java.io.Serializable

data class Trip (
    var trip_id: String,
    var trip_headsign: String,
    var route_id: String,
    var block_id: String,
    var direction: String,
    var service_id: String,
    var shape_id: String
) : Serializable