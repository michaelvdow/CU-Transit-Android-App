package com.apps.michaeldow.cutransitcompanion.route

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure

class RouteViewModel(application: Application): AndroidViewModel(application){

    lateinit var departure: Departure

}