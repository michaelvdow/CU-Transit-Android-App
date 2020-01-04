package com.apps.michaedow.cutransit.Utils

class Utils {

    companion object {
        fun fixStopId(stopId: String): String {
            var fixedStopId = stopId
            val index = fixedStopId.indexOf(":")
            if (index != -1) {
                fixedStopId = fixedStopId.substring(0, index)
            }
            return fixedStopId
        }
    }

}