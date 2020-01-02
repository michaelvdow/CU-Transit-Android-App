package com.apps.michaedow.cutransit.Utils

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task


class BetterLocationProvider(context: Context) {

    companion object {
        lateinit var instance: BetterLocationProvider
    }

    init {
        instance = this
    }

    private val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
        }
    }

    init {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        client.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun updateLocation(): Task<Location>? {
        return client.lastLocation
    }


}