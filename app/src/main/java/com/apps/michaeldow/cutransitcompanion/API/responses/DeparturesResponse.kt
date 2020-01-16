package com.apps.michaeldow.cutransitcompanion.API.responses

import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure

data class DeparturesResponse (
    val departures: List<Departure>?
)