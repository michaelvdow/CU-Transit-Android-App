package com.apps.michaedow.cutransit.route

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.apps.michaedow.cutransit.API.responses.departureResponse.Departure

class RouteViewModel(application: Application): AndroidViewModel(application){

    lateinit var departure: Departure

}