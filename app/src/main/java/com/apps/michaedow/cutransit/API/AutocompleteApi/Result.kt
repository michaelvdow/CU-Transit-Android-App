package com.apps.michaedow.cutransit.API.AutocompleteApi

data class Result (
    var id: String,
    var smsCode: String,
    var name: String,
    var city: String,
    var isParent: Boolean,
    var rank: Int
)