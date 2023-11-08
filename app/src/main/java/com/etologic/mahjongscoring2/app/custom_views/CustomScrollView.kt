/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.etologic.mahjongscoring2.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class CustomScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {

    override fun computeVerticalScrollRange(): Int {
        // Check if scrolling is needed, and show the vertical scrollbar accordingly
        val range = super.computeVerticalScrollRange()
        isVerticalScrollBarEnabled = range > height
        return range
    }
}