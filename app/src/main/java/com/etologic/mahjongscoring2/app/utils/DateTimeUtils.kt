/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.utils

import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    
    private const val DATE_FORMAT = "EEEE d MMMM yyyy HH:mm"
    
    fun getPrettyDate(date: Date): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return StringUtils.capitalize(sdf.format(date))
    }
    
    fun areEqual(date1: Date?, date2: Date?): Boolean {
        if (date1 == null && date2 == null) return true
        return if (date1 == null || date2 == null) false else date1 === date2
    }
}
