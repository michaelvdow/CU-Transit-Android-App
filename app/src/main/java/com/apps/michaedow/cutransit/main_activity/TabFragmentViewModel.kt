package com.apps.michaedow.cutransit.main_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaedow.cutransit.API.AutocompleteApi.AutocompleteApiFactory
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

    private val repository = TabFragmentDatabaseProvider(AutocompleteApiFactory.autocompleteApi)

    private val mutableSuggestions: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val suggestions: LiveData<ArrayList<String>>
        get() = mutableSuggestions

    fun search(query: String) {
        scope.launch {
            val result = repository.search(query)
            if (result != null) {
                val newSuggestions = ArrayList<String>()
                for (suggestion in result) {
                    newSuggestions.add(suggestion.result.name)
                }
                mutableSuggestions.postValue(newSuggestions)
            }
        }
    }
}