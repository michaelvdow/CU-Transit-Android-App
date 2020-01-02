package com.apps.michaedow.cutransit.main_activity.near_me

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaedow.cutransit.Utils.BetterLocationProvider
import com.apps.michaedow.cutransit.database.Stops.StopDatabase
import com.apps.michaedow.cutransit.database.Stops.StopItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NearMeViewModel(application: Application): AndroidViewModel(application) {

    private val database: NearMeDatabaseProvider
    private var locationClient: BetterLocationProvider = BetterLocationProvider.instance
    private var nearbyStopsJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + nearbyStopsJob)

    // Live data objects
    private val mutableStops = MutableLiveData<List<StopItem>>()
    val stops: LiveData<List<StopItem>>
        get() = mutableStops

    private val mutableLocation = MutableLiveData<Location>()
    val location: LiveData<Location>
        get() = mutableLocation

    private val mutableRefreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean>
        get() = mutableRefreshing

    init {
        database = NearMeDatabaseProvider(StopDatabase.getDatabase(getApplication<Application>().applicationContext).stopDao())
        mutableRefreshing.value = false
    }

    fun updateLocation() {
        locationClient = BetterLocationProvider.instance
        if (ActivityCompat.checkSelfPermission(getApplication<Application>().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getApplication<Application>().applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mutableRefreshing.value = true
            locationClient.updateLocation()?.addOnSuccessListener { newLocation: Location? ->
                if (newLocation != null) {
                    mutableLocation.value = newLocation
                    uiScope.launch {
                        mutableStops.value = database.getNearbyStops(newLocation)
                        mutableRefreshing.value = false
                    }
                } else {
                    mutableRefreshing.value = false
                }
            }
        } else {
            mutableRefreshing.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        nearbyStopsJob.cancel()
    }

}