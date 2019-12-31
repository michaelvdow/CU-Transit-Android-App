package com.apps.michaedow.cutransit.API

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MtdApi {

    @GET("GetDeparturesByStop")
    fun getDeparturesByStop(@Query("stop_id") stopId: String, @Query("pt") pt: Int
                            , @Query("count") count: Int): Deferred<Response<DeparturesResponse>>

}