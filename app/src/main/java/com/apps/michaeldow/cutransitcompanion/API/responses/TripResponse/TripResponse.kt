package com.apps.michaeldow.cutransitcompanion.API.responses.TripResponse

data class TripResponse(
    var itineraries: List<Itinerary>?
) {
    data class Itinerary(
        var end_time: String,
        var legs: List<Leg>,
        var start_time: String,
        var travel_time: Int
    ) {
        data class Leg(
            var services: List<Service>,
            var type: String,
            var walk: Walk
        ) {
            data class Service(
                var begin: Begin,
                var end: End,
                var route: Route,
                var trip: Trip
            ) {
                data class Begin(
                    var lat: Double,
                    var lon: Double,
                    var name: String,
                    var stop_id: String,
                    var time: String
                )

                data class End(
                    var lat: Double,
                    var lon: Double,
                    var name: String,
                    var stop_id: String,
                    var time: String
                )

                data class Route(
                    var route_color: String,
                    var route_id: String,
                    var route_long_name: String,
                    var route_short_name: String,
                    var route_text_color: String
                )

                data class Trip(
                    var block_id: String,
                    var direction: String,
                    var route_id: String,
                    var service_id: String,
                    var shape_id: String,
                    var trip_headsign: String,
                    var trip_id: String
                )
            }

            data class Walk(
                var begin: Begin,
                var direction: String,
                var distance: Double,
                var end: End
            ) {
                data class Begin(
                    var lat: Double,
                    var lon: Double,
                    var name: String,
                    var time: String
                )

                data class End(
                    var lat: Double,
                    var lon: Double,
                    var name: String,
                    var stop_id: String,
                    var time: String
                )
            }
        }
    }
}