package com.apps.michaedow.cutransit.API

import com.apps.michaedow.cutransit.API.responses.DeparturesResponse
import com.apps.michaedow.cutransit.API.responses.ShapeResponse
import com.apps.michaedow.cutransit.API.responses.busLocationResponse.BusLocationResponse
import com.apps.michaedow.cutransit.API.responses.stopTimesResponse.StopTimesResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MtdApi {

    @GET("GetDeparturesByStop")
    fun getDeparturesByStop(@Query("stop_id") stopId: String, @Query("pt") pt: Int
                            , @Query("count") count: Int): Deferred<Response<DeparturesResponse>>

    @GET("getshape")
    fun getShape(@Query("shape_id") shapeId: String): Deferred<Response<ShapeResponse>>

    @GET("getstoptimesbytrip")
    fun getStopTimesByTrip(@Query("trip_id") tripId: String): Deferred<Response<StopTimesResponse>>

    @GET("getvehicle")
    fun getBusLocation(@Query("vehicle_id") vehicleId: String): Deferred<Response<BusLocationResponse>>


}