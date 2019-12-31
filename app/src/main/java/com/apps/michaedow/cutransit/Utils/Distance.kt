package com.apps.michaedow.cutransit.Utils

import android.location.Location

class Distance {

    companion object {
        fun calculateDistance(location: Location?, lat: Double, lon: Double, metric: Boolean): String {
            if (location != null) {
                val R = 3959.0 // Radius of the earth in km
                val dLat: Double = deg2rad(lat - location.latitude)
                val dLon: Double = deg2rad(lon - location.longitude)
                val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(location.latitude)) * Math.cos(deg2rad(lat)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
                val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                val d = R * c
                if (metric) {
                    return String.format("%.2f", milesToKm(d)) + " km"
                } else {
                    return String.format("%.2f", d) + " mi"
                }
            } else {
                return ""
            }
        }

        fun milesToKm(miles: Double): Double {
            return miles * 1.60934
        }

        private fun deg2rad(deg: Double): Double {
            return deg * (Math.PI / 180)
        }

    }

}