package com.apps.michaeldow.cutransitcompanion.views.main_activity.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaeldow.cutransitcompanion.database.Stops.StopDatabase
import com.apps.michaeldow.cutransitcompanion.database.Stops.StopItem
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MapViewModel(application: Application): AndroidViewModel(application) {

    private val database: MapDatabaseProvider

    private var job = Job()
    private val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)


    private val mutableStops: MutableLiveData<List<StopItem>> = MutableLiveData()
    val stops: LiveData<List<StopItem>>
        get() = mutableStops

    var currentLocation: LatLng? = null

    init {
        database = MapDatabaseProvider(StopDatabase.getDatabase(getApplication<Application>().applicationContext).stopDao())
        scope.launch {
            mutableStops.postValue(database.getStops())
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}