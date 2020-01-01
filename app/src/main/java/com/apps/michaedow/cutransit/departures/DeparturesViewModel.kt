package com.apps.michaedow.cutransit.departures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaedow.cutransit.API.ApiFactory
import com.apps.michaedow.cutransit.API.Departure
import com.apps.michaedow.cutransit.database.Favorites.FavoritesDatabase
import com.apps.michaedow.cutransit.database.Stops.StopDatabase
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

    var stopName: String = ""

    fun updateDepartures() {
        mutableRefreshing.postValue(true)
        scope.launch {
            mutableDepartures.postValue(repository.getDepartures(stopName))
            mutableRefreshing.postValue(false)
        }
    }

    fun favoriteClicked() {
        scope.launch {
            val isClicked = repository.updateFavorite(stopName)
            mutableIsFavorite.postValue(isClicked)
        }
    }

    fun checkIfFavorite() {
        scope.launch {
            val isClicked = repository.isFavorite(stopName)
            mutableIsFavorite.postValue(isClicked)
        }
    }
}
