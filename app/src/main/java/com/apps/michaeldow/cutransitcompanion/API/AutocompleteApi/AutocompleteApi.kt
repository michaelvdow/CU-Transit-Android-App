package com.apps.michaeldow.cutransitcompanion.API.AutocompleteApi

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AutocompleteApi {

    @GET("suggest/{query}")
    fun getAutocomplete(@Path("query") stopId: String): Deferred<Response<List<Suggestion>>>

}