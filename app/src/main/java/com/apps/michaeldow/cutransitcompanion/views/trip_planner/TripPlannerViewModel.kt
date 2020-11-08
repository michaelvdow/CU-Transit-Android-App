package com.apps.michaeldow.cutransitcompanion.views.trip_planner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.apps.michaeldow.cutransitcompanion.API.ApiFactory
import com.apps.michaeldow.cutransitcompanion.API.responses.TripResponse.TripResponse
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TripPlannerViewModel(application: Application): AndroidViewModel(application) {

    // Coroutine
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository = TripPlannerDatabaseProvider(ApiFactory.mtdApi)

    private val mutableStartFeature = MutableLiveData<CarmenFeature?>()
    val startFeature: MutableLiveData<CarmenFeature?>
        get() = mutableStartFeature

    private val mutableEndFeature = MutableLiveData<CarmenFeature?>()
    val endFeature: MutableLiveData<CarmenFeature?>
        get() = mutableEndFeature

    private val mutableTripResult = MutableLiveData<TripResponse?>()
    val tripResult: MutableLiveData<TripResponse?>
        get() = mutableTripResult


    fun setStart(feature: CarmenFeature) {
        mutableStartFeature.postValue(feature)
        search(feature, endFeature.value)
    }

    fun setEnd(feature: CarmenFeature) {
        mutableEndFeature.postValue(feature)
        search(startFeature.value, feature)
    }

    private fun search(start: CarmenFeature?, end: CarmenFeature?) {
        if (start != null && end != null) {
            scope.launch {
                val result = repository.search(start, end)
                mutableTripResult.postValue(result)
            }
        }
    }


}