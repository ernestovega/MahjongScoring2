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

package com.etologic.mahjongscoring2.business.model.entities

import com.etologic.mahjongscoring2.app.base.RecyclerViewable
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound

data class UiRound(
    var dbRound: DbRound
) : RecyclerViewable<UiRound>() {

    constructor() : this(DbRound(-1, -1))

    var roundNumber: Int = 0
    var pointsP1: Int = 0
    var pointsP2: Int = 0
    var pointsP3: Int = 0
    var pointsP4: Int = 0
    var totalPointsP1: Int = 0
    var totalPointsP2: Int = 0
    var totalPointsP3: Int = 0
    var totalPointsP4: Int = 0
    var isBestHand: Boolean = false

    fun isNotEnded(): Boolean =
        dbRound.winnerInitialSeat == null

    fun areTherePenalties(): Boolean =
        dbRound.penaltyP1 != 0 ||
                dbRound.penaltyP2 != 0 ||
                dbRound.penaltyP3 != 0 ||
                dbRound.penaltyP4 != 0

    override fun compareIdTo(`object`: UiRound): Boolean =
        dbRound.gameId == `object`.dbRound.gameId &&
                dbRound.roundId == `object`.dbRound.roundId

    override fun compareContentsTo(`object`: UiRound): Boolean =
        areEqual(this, `object`)

    override fun getCopy(): UiRound = this.copy().apply {
        this@apply.roundNumber = this@UiRound.roundNumber
        this@apply.pointsP1 = this@UiRound.pointsP1
        this@apply.pointsP2 = this@UiRound.pointsP2
        this@apply.pointsP3 = this@UiRound.pointsP3
        this@apply.pointsP4 = this@UiRound.pointsP4
        this@apply.totalPointsP1 = this@UiRound.totalPointsP1
        this@apply.totalPointsP2 = this@UiRound.totalPointsP2
        this@apply.totalPointsP3 = this@UiRound.totalPointsP3
        this@apply.totalPointsP4 = this@UiRound.totalPointsP4
        this@apply.isBestHand = this@UiRound.isBestHand
    }

    companion object {
        fun areEqual(rounds1: List<UiRound>?, rounds2: List<UiRound>?): Boolean {
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

        private fun areEqual(round1: UiRound, round2: UiRound): Boolean {
            return round1.dbRound.gameId == round2.dbRound.gameId &&
                    round1.dbRound.roundId == round2.dbRound.roundId &&
                    round1.dbRound.handPoints == round2.dbRound.handPoints &&
                    round1.dbRound.winnerInitialSeat === round2.dbRound.winnerInitialSeat &&
                    round1.dbRound.discarderInitialSeat === round2.dbRound.discarderInitialSeat &&
                    round1.dbRound.penaltyP1 == round2.dbRound.penaltyP1 &&
                    round1.dbRound.penaltyP2 == round2.dbRound.penaltyP2 &&
                    round1.dbRound.penaltyP3 == round2.dbRound.penaltyP3 &&
                    round1.dbRound.penaltyP4 == round2.dbRound.penaltyP4 &&
                    round1.roundNumber == round2.roundNumber &&
                    round1.pointsP1 == round2.pointsP1 &&
                    round1.pointsP2 == round2.pointsP2 &&
                    round1.pointsP3 == round2.pointsP3 &&
                    round1.pointsP4 == round2.pointsP4 &&
                    round1.totalPointsP1 == round2.totalPointsP1 &&
                    round1.totalPointsP2 == round2.totalPointsP2 &&
                    round1.totalPointsP3 == round2.totalPointsP3 &&
                    round1.totalPointsP4 == round2.totalPointsP4 &&
                    round1.isBestHand == round2.isBestHand
        }
    }
}