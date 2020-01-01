package com.apps.michaedow.cutransit.API.AutocompleteApi

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AutocompleteApi {

    @GET("suggest/{query}")
    fun getAutocomplete(@Path("query") stopId: String): Deferred<Response<List<Suggestion>>>

}