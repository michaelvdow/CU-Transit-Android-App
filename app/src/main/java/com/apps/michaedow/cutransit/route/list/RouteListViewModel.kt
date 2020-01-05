package com.apps.michaedow.cutransit.route.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaedow.cutransit.API.ApiFactory
import com.apps.michaedow.cutransit.API.responses.departureResponse.Departure
import com.apps.michaedow.cutransit.API.responses.stopTimesResponse.StopTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RouteListViewModel(application: Application): AndroidViewModel(application) {

    // Coroutine
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository = RouteListDatabaseProvider(ApiFactory.mtdApi)

    // Data
    private val mutableStopTimes = MutableLiveData<List<StopTime>>(emptyList())
    val stopTimes: LiveData<List<StopTime>>
        get() = mutableStopTimes

    lateinit var departure: Departure


    fun getStops() {
        scope.launch {
            val stopTimes = repository.getStops(departure.trip.trip_id)
            if (stopTimes != null) {
                mutableStopTimes.postValue(stopTimes)
            }
        }
    }

}