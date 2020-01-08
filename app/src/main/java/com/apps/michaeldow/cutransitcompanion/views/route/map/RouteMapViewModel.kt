package com.apps.michaeldow.cutransitcompanion.views.route.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaeldow.cutransitcompanion.API.ApiFactory
import com.apps.michaeldow.cutransitcompanion.API.responses.busLocationResponse.Vehicle
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure
import com.apps.michaeldow.cutransitcompanion.API.responses.shapeResponse.Shape
import com.apps.michaeldow.cutransitcompanion.API.responses.stopTimesResponse.StopTime
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

    private var mutableBus: MutableLiveData<Vehicle> = MutableLiveData()
    val bus: LiveData<Vehicle>
        get() = mutableBus

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
                mutableBus.postValue(bus)
            }
        }
    }

}