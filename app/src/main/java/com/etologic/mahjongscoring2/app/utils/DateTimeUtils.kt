package com.etologic.mahjongscoring2.app.utils

import com.etologic.mahjongscoring2.business.model.entities.Round
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    
    //CONSTANTS
    private const val HOUR_MILLIS = (1000 * 60 * 60).toLong()
    private const val HOUR_MINUTES: Long = 60
    private const val DATE_FORMAT = "EEEE d MMMM yyyy HH:mm"
    private const val TIME_FORMAT = "HH:mm:ss"
    private const val NO_DURATION = "-"
    
    fun getPrettyDate(date: Date): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
        return sdf.format(date)
    }
    
    fun getPrettyTime(time: Long): String {
        return if (time <= 0) {
            NO_DURATION
        } else {
            val sdf = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH)
            sdf.format(time)
        }
    }
    
    fun getPrettyDuration(rounds: List<Round>?): String {
        return if (rounds != null) {
            var duration: Long = 0
            for (round in rounds) {
                duration += round.roundDuration
            }
            val hours = TimeUnit.MILLISECONDS.toHours(duration)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - hours * HOUR_MINUTES
            String.format(Locale.getDefault(), "%2dh %2dm", hours, minutes)
        } else "-"
    }
    
    fun areEqual(date1: Date?, date2: Date?): Boolean {
        if (date1 == null && date2 == null) return true
        return if (date1 == null || date2 == null) false else date1 === date2
    }
}
