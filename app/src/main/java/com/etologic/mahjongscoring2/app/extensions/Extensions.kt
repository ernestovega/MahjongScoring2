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
package com.etologic.mahjongscoring2.app.extensions

import android.os.SystemClock
import android.view.View
import android.view.View.OnClickListener

internal fun View.setOnSecureClickListener(
    debounceTime: Long = 600L,
    action: (view: View) -> Unit
) {
    this.setOnClickListener(object : OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action(v)

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun <T> List<T>.second(): T {
    if (size < 2)
        throw NoSuchElementException("Not enough elements")
    return this[1]
}

fun <T> List<T>.third(): T {
    if (size < 3)
        throw NoSuchElementException("Not enough elements")
    return this[2]
}

fun <T> List<T>.fourth(): T {
    if (size < 4)
        throw NoSuchElementException("Not enough elements")
    return this[3]
}

fun <T> Array<T>.second(): T {
    if (size < 2)
        throw NoSuchElementException("Not enough elements")
    return this[1]
}

fun <T> Array<T>.third(): T {
    if (size < 3)
        throw NoSuchElementException("Not enough elements")
    return this[2]
}

fun <T> Array<T>.fourth(): T {
    if (size < 4)
        throw NoSuchElementException("Not enough elements")
    return this[3]
}

fun Int?.toStringOrHyphen(): String = this?.toString() ?: "-"

