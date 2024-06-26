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

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.business.model.entities.UiRound.Companion.NOT_SET_ROUND_ID
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.converters.TableWindsConverter

@Entity(
    tableName = "Rounds",
    foreignKeys = [ForeignKey(
        entity = DbGame::class,
        parentColumns = ["gameId"],
        childColumns = ["gameId"],
    )],
    indices = [Index(
        value = ["gameId", "roundId"],
        unique = true,
    )],
)
data class DbRound(
    val gameId: GameId,
    @field:PrimaryKey(autoGenerate = true) val roundId: RoundId,
) {
    @TypeConverters(TableWindsConverter::class)
    var winnerInitialSeat: TableWinds? = null

    @TypeConverters(TableWindsConverter::class)
    var discarderInitialSeat: TableWinds? = null
    var handPoints: Int = 0
    var penaltyP1: Int = 0
    var penaltyP2: Int = 0
    var penaltyP3: Int = 0
    var penaltyP4: Int = 0

    constructor(
        gameId: GameId,
        roundId: RoundId = NOT_SET_ROUND_ID,
        winnerInitialSeat: TableWinds?,
        discarderInitialSeat: TableWinds?,
        handPoints: Int = 0,
        penaltyP1: Int = 0,
        penaltyP2: Int = 0,
        penaltyP3: Int = 0,
        penaltyP4: Int = 0,
    ) : this(gameId, roundId) {
        this.winnerInitialSeat = winnerInitialSeat
        this.discarderInitialSeat = discarderInitialSeat
        this.handPoints = handPoints
        this.penaltyP1 = penaltyP1
        this.penaltyP2 = penaltyP2
        this.penaltyP3 = penaltyP3
        this.penaltyP4 = penaltyP4
    }

    fun isOngoing(): Boolean = winnerInitialSeat == null
}