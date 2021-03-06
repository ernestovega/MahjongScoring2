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
package com.etologic.mahjongscoring2.business.model.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.DateConverter
import java.util.*

@Entity(
    tableName = "Games",
    indices = [Index(value = ["gameId"], unique = true)]
)
class Game(
    @field:PrimaryKey(autoGenerate = true) val gameId: Long,
    var nameP1: String = "",
    var nameP2: String = "",
    var nameP3: String = "",
    var nameP4: String = "",
    @TypeConverters(DateConverter::class) val startDate: Date
) {
    
    companion object {
        private const val NOT_SET_GAME_ID: Long = 0
    }
    
    constructor(defaultNames: Array<String>) : this(
        NOT_SET_GAME_ID,
        defaultNames[0],
        defaultNames[1],
        defaultNames[2],
        defaultNames[3],
        Calendar.getInstance().time
    )
    
    val copy: Game
        get() = Game(
            gameId,
            nameP1,
            nameP2,
            nameP3,
            nameP4,
            startDate
        )
    
    fun getPlayersNames(): Array<String> = arrayOf(nameP1, nameP2, nameP3, nameP4)
    
    fun getPlayerNameByInitialPosition(initialPosition: TableWinds): String {
        return when (initialPosition) {
            NONE -> ""
            EAST -> nameP1
            SOUTH -> nameP2
            WEST -> nameP3
            NORTH -> nameP4
        }
    }
}