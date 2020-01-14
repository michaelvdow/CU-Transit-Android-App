package com.apps.michaeldow.cutransitcompanion.Utils

import android.graphics.Color
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
            try {
                val format = SimpleDateFormat("HH:mm:ss")
                val date: Date = format.parse(time)

                val betterFormat: DateFormat = SimpleDateFormat("h:mm a")
                var betterDate = betterFormat.format(date)
                if (betterDate.length == 7) {
                    return " $betterDate"
                }
                return betterDate
            } catch (e: java.lang.Exception) {
                return ""
            }
        }

        fun fixLastUpdatedTime(time: String): String {
            // 2013-03-04T15:19:33-06:00
            try {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
                val date: Date = format.parse(time)

                val betterFormat: DateFormat = SimpleDateFormat("h:mm a")
                var betterDate = betterFormat.format(date)
                return "Updated: $betterDate"
            } catch (e: Exception) {
                return ""
            }
        }

        fun getTextColor(color: Int): Int {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return if (red * 0.299 + green * 0.587 + blue * 0.114 > 186) {
                Color.parseColor("#000000")
            } else {
                Color.parseColor("#ffffff")
            }
        }
    }

}