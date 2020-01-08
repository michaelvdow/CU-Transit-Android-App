package com.apps.michaeldow.cutransitcompanion.views.main_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaeldow.cutransitcompanion.API.AutocompleteApi.AutocompleteApiFactory
import com.apps.michaeldow.cutransitcompanion.database.Stops.StopDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TabFragmentViewModel(application: Application): AndroidViewModel(application) {

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository = TabFragmentDatabaseProvider(AutocompleteApiFactory.autocompleteApi,
        StopDatabase.getDatabase(getApplication<Application>().applicationContext).stopDao())

    private val mutableSuggestions: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val suggestions: LiveData<ArrayList<String>>
        get() = mutableSuggestions

    val mutableStopId = MutableLiveData<String?>(null)

    fun search(query: String) {
        scope.launch {
            val result = repository.search(query)
            if (result != null) {
                val newSuggestions = ArrayList<String>()
                for (suggestion in result) {
                    println(suggestion.result.name)
                    newSuggestions.add(suggestion.result.name)
                }
                mutableSuggestions.postValue(newSuggestions)
            }
        }
    }

    fun openDepartureFromStopName(stopName: String) {
        scope.launch {
            val stopId = repository.getStopId(stopName)
            if (stopId != null) {
                mutableStopId.postValue(stopId)
            }
        }
    }
}