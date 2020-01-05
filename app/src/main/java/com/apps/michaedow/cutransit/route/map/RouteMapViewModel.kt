package com.apps.michaedow.cutransit.route.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaedow.cutransit.API.ApiFactory
import com.apps.michaedow.cutransit.API.responses.departureResponse.Departure
import com.apps.michaedow.cutransit.API.responses.shapeResponse.Shape
import com.apps.michaedow.cutransit.API.responses.stopTimesResponse.StopTime
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RouteMapViewModel(application: Application): AndroidViewModel(application) {

    // Coroutine
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : RouteMapDatabaseProvider = RouteMapDatabaseProvider(ApiFactory.mtdApi)

    // Data
    lateinit var departure: Departure
    private var mutableShapes: MutableLiveData<List<Shape>?> = MutableLiveData()
    val shapes: LiveData<List<Shape>?>
        get() = mutableShapes

    private var mutableStopTimes: MutableLiveData<List<StopTime>?> = MutableLiveData()
    val stopTimes: LiveData<List<StopTime>?>
        get() = mutableStopTimes

    private var mutableBusLocation: MutableLiveData<LatLng> = MutableLiveData()
    val busLocation: LiveData<LatLng>
        get() = mutableBusLocation

    fun getShape() {
        scope.launch {
            mutableShapes.postValue(repository.getShapes(departure.trip.shape_id))
        }
    }

    fun getStops() {
        scope.launch {
            mutableStopTimes.postValue(repository.getStopTimes(departure.trip.trip_id))
        }
    }

    fun updateBusLocation() {
        scope.launch {
            val bus = repository.getBusLocation(departure.vehicle_id)
            if (bus != null) {
                mutableBusLocation.postValue(LatLng(bus.location.lat, bus.location.lon))
            }
        }
    }

}