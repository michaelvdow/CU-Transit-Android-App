package com.apps.michaedow.cutransit.API.responses

import com.apps.michaedow.cutransit.API.responses.departureResponse.Departure

data class DeparturesResponse (
    val departures: List<Departure>
)