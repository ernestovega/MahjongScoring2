/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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
package com.etologic.mahjongscoring2.data_source.local_data_sources.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.converters.DateConverter
import java.util.Date

@Entity(
    tableName = "Games",
    indices = [Index(
        value = ["gameId"],
        unique = true,
    )],
)
data class DbGame(
    @field:PrimaryKey(autoGenerate = true) val gameId: GameId,
    var nameP1: String = "",
    var nameP2: String = "",
    var nameP3: String = "",
    var nameP4: String = "",
    @TypeConverters(DateConverter::class) val startDate: Date,
    @TypeConverters(DateConverter::class) var endDate: Date? = null,
    @ColumnInfo(defaultValue="") var gameName: String = "",
)
