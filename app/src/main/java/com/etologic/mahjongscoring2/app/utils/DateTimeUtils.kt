package com.etologic.mahjongscoring2.app.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    
    //CONSTANTS
    private const val DATE_FORMAT = "EEEE d MMMM yyyy HH:mm"
    
    fun getPrettyDate(date: Date): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
        return sdf.format(date)
    }
    
    fun areEqual(date1: Date?, date2: Date?): Boolean {
        if (date1 == null && date2 == null) return true
        return if (date1 == null || date2 == null) false else date1 === date2
    }
}
