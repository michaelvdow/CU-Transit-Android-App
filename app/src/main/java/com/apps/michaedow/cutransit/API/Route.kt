package com.apps.michaedow.cutransit.API

import java.io.Serializable

data class Route (
    var route_color: String,
    var route_id: String,
    var route_long_name: String,
    var route_short_name: String,
    var route_text_color: String
) : Serializable