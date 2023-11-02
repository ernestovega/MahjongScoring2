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
package com.etologic.mahjongscoring2.business.model.enums

import com.etologic.mahjongscoring2.business.model.entities.Table

enum class TableWinds(val code: Int) {
    NONE(-1),
    EAST(0),
    SOUTH(1),
    WEST(2),
    NORTH(3);

    companion object {
        val asArray = arrayOf(EAST, SOUTH, WEST, NORTH)
    }
}
