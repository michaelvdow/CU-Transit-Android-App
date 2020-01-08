package com.apps.michaeldow.cutransitcompanion.Utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

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

        fun fixStopTime(time: String): String {
            val format = SimpleDateFormat("HH:mm:ss")
            val date: Date = format.parse(time)

            val betterFormat: DateFormat = SimpleDateFormat("h:mm a")
            var betterDate = betterFormat.format(date)
            if (betterDate.length == 7) {
                return " $betterDate"
            }
            return betterDate
        }

        fun fixLastUpdatedTime(time: String): String {
            // 2013-03-04T15:19:33-06:00
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            val date: Date = format.parse(time)

            val betterFormat: DateFormat = SimpleDateFormat("h:mm a")
            var betterDate = betterFormat.format(date)
            return "Last updated: $betterDate"
        }
    }

}