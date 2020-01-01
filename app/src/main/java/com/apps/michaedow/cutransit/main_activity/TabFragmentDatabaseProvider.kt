package com.apps.michaedow.cutransit.main_activity

import com.apps.michaedow.cutransit.API.AutocompleteApi.AutocompleteApi
import com.apps.michaedow.cutransit.API.AutocompleteApi.Suggestion
import com.apps.michaedow.cutransit.API.BaseRepository

class TabFragmentDatabaseProvider(private val api: AutocompleteApi): BaseRepository() {

    suspend fun search(query: String): List<Suggestion>? {
        return safeApiCall(
            call = {api.getAutocomplete(query).await()},
            errorMessage = "WHOOPS"
        )
    }

}