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
package com.etologic.mahjongscoring2.business.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.etologic.mahjongscoring2.app.base.RecyclerViewable
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.TableWindsConverter
import com.etologic.mahjongscoring2.data_source.model.DBGame
import com.etologic.mahjongscoring2.data_source.model.GameId

typealias RoundId = Long

@Entity(
    tableName = "Rounds",
    foreignKeys = [ForeignKey(entity = DBGame::class, parentColumns = ["gameId"], childColumns = ["gameId"])],
    indices = [Index(value = ["gameId", "roundId"], unique = true)]
)
data class Round(
    val gameId: GameId,
    @field:PrimaryKey(autoGenerate = true) val roundId: RoundId
) : RecyclerViewable<Round>() {

    @TypeConverters(TableWindsConverter::class)
    var winnerInitialSeat = NONE

    @TypeConverters(TableWindsConverter::class)
    var discarderInitialSeat = NONE
    var handPoints = 0
    var pointsP1 = 0
    var pointsP2 = 0
    var pointsP3 = 0
    var pointsP4 = 0
    var penaltyP1 = 0
    var penaltyP2 = 0
    var penaltyP3 = 0
    var penaltyP4 = 0
    var isEnded = false

    @Ignore
    var roundNumber: Int = 0

    @Ignore
    var totalPointsP1 = 0

    @Ignore
    var totalPointsP2 = 0

    @Ignore
    var totalPointsP3 = 0

    @Ignore
    var totalPointsP4 = 0

    @Ignore
    var isBestHand = false

    constructor(gameId: GameId) : this(gameId, NOT_SET_ROUND_ID)

    override fun compareIdTo(`object`: Round): Boolean = gameId == `object`.gameId && roundId == `object`.roundId

    override fun compareContentsTo(`object`: Round): Boolean = areEqual(this, `object`)

    override fun getCopy(): Round = copy().apply {
        this.winnerInitialSeat = this@Round.winnerInitialSeat
        this.discarderInitialSeat = this@Round.discarderInitialSeat
        this.handPoints = this@Round.handPoints
        this.pointsP1 = this@Round.pointsP1
        this.pointsP2 = this@Round.pointsP2
        this.pointsP3 = this@Round.pointsP3
        this.pointsP4 = this@Round.pointsP4
        this.penaltyP1 = this@Round.penaltyP1
        this.penaltyP2 = this@Round.penaltyP2
        this.penaltyP3 = this@Round.penaltyP3
        this.penaltyP4 = this@Round.penaltyP4
        this.isEnded = this@Round.isEnded
        this.roundNumber = this@Round.roundNumber
        this.totalPointsP1 = this@Round.totalPointsP1
        this.totalPointsP2 = this@Round.totalPointsP2
        this.totalPointsP3 = this@Round.totalPointsP3
        this.totalPointsP4 = this@Round.totalPointsP4
        this.isBestHand = this@Round.isBestHand
    }

    companion object {

        private const val NOT_SET_ROUND_ID: RoundId = 0

        fun areEqual(rounds1: List<Round>?, rounds2: List<Round>?): Boolean {
            if (rounds1 == null && rounds2 == null)
                return true
            else if (rounds1 != null && rounds2 != null) {
                if (rounds1.size != rounds2.size)
                    return false
                else {
                    for (i in rounds1.indices) {
                        if (!areEqual(rounds1[i], rounds2[i]))
                            return false
                    }
                    return true
                }
            } else
                return false
        }

        private fun areEqual(round1: Round, round2: Round): Boolean {
            return round1.gameId == round2.gameId &&
                    round1.roundId == round2.roundId &&
                    round1.roundNumber == round2.roundNumber &&
                    round1.handPoints == round2.handPoints &&
                    round1.winnerInitialSeat === round2.winnerInitialSeat &&
                    round1.discarderInitialSeat === round2.discarderInitialSeat &&
                    round1.pointsP1 == round2.pointsP1 &&
                    round1.pointsP2 == round2.pointsP2 &&
                    round1.pointsP3 == round2.pointsP3 &&
                    round1.pointsP4 == round2.pointsP4 &&
                    round1.totalPointsP1 == round2.totalPointsP1 &&
                    round1.totalPointsP2 == round2.totalPointsP2 &&
                    round1.totalPointsP3 == round2.totalPointsP3 &&
                    round1.totalPointsP4 == round2.totalPointsP4 &&
                    round1.penaltyP1 == round2.penaltyP1 &&
                    round1.penaltyP2 == round2.penaltyP2 &&
                    round1.penaltyP3 == round2.penaltyP3 &&
                    round1.penaltyP4 == round2.penaltyP4 &&
                    round1.isBestHand == round2.isBestHand &&
                    round1.isEnded == round2.isEnded
        }
    }
}

fun Round.applyPenaltiesAndMarkRoundAsEnded() {
    fun applyPlayersPenalties() {
        pointsP1 += penaltyP1
        pointsP2 += penaltyP2
        pointsP3 += penaltyP3
        pointsP4 += penaltyP4
    }

    applyPlayersPenalties()
    isEnded = true
}

fun Round.areTherePenalties(): Boolean = penaltyP1 != 0 || penaltyP2 != 0 || penaltyP3 != 0 || penaltyP4 != 0