package com.apps.michaeldow.cutransitcompanion.views.departures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaeldow.cutransitcompanion.API.ApiFactory
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesDatabase
import com.apps.michaeldow.cutransitcompanion.database.Stops.StopDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DeparturesViewModel(application: Application): AndroidViewModel(application) {

    // Coroutine
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : DeparturesDatabaseProvider = DeparturesDatabaseProvider(ApiFactory.mtdApi,
        StopDatabase.getDatabase(getApplication<Application>().applicationContext).stopDao(),
        FavoritesDatabase.getDatabase(getApplication<Application>().applicationContext).favoritesDao())

    // Live Data objects
    private val mutableDepartures: MutableLiveData<MutableList<Departure>> = MutableLiveData()
    val departures: LiveData<MutableList<Departure>>
        get() = mutableDepartures

    private val mutableRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val refreshing: LiveData<Boolean>
        get() = mutableRefreshing

    private val mutableIsFavorite: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFavorite: LiveData<Boolean>
        get() = mutableIsFavorite

    private val mutableStopName: MutableLiveData<String> = MutableLiveData("")
    val stopName: LiveData<String>
        get() = mutableStopName

    var stopId: String = ""

    private val refreshLimit: Long = 30000
    private var lastRefresh: Long = 0

    fun updateDepartures() {
        mutableRefreshing.postValue(true)
        if (lastRefresh + refreshLimit < System.currentTimeMillis()) {
            lastRefresh = System.currentTimeMillis()
            scope.launch {
                mutableDepartures.postValue(repository.getDepartures(stopId))
                mutableRefreshing.postValue(false)
            }
        } else {
            mutableRefreshing.postValue(false)
        }
    }

    fun favoriteClicked() {
        scope.launch {
            val isClicked = repository.updateFavorite(stopName.value as String, stopId)
            mutableIsFavorite.postValue(isClicked)
        }
    }

    fun checkIfFavorite() {
        scope.launch {
            val isClicked = repository.isFavorite(stopId)
            mutableIsFavorite.postValue(isClicked)
        }
    }

    fun getStopName() {
        scope.launch {
            mutableStopName.postValue(repository.getStopName(stopId))
        }
    }
}
