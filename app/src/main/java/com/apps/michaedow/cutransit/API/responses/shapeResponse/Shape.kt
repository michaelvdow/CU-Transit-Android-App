package com.apps.michaedow.cutransit.API.responses.shapeResponse

data class Shape (
    var shape_dist_traveled: Double,
    var shape_pt_lat: Double,
    var shape_pt_lon: Double,
    var shape_pt_sequence: Int
)