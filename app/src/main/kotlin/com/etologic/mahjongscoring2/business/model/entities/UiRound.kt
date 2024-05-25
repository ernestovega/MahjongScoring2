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
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import com.etologic.mahjongscoring2.business.model.enums.TableWinds

typealias RoundId = Long

data class UiRound(
    val gameId: GameId,
    val roundId: RoundId,
    val winnerInitialSeat: TableWinds?,
    val discarderInitialSeat: TableWinds?,
    val handPoints: Int,
    val penaltyP1: Int,
    val penaltyP2: Int,
    val penaltyP3: Int,
    val penaltyP4: Int,
) : RecyclerViewable<UiRound>() {

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

    constructor() : this(
        gameId = NOT_SET_GAME_ID,
        roundId = NOT_SET_ROUND_ID,
        winnerInitialSeat = null,
        discarderInitialSeat = null,
        handPoints = 0,
        penaltyP1 = 0,
        penaltyP2 = 0,
        penaltyP3 = 0,
        penaltyP4 = 0,
    )

    fun areTherePenalties(): Boolean =
        penaltyP1 != 0 || penaltyP2 != 0 || penaltyP3 != 0 || penaltyP4 != 0

    override fun compareIdTo(`object`: UiRound): Boolean =
        gameId == `object`.gameId &&
                roundId == `object`.roundId

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

    fun isOngoing(): Boolean = winnerInitialSeat == null

    companion object {
        const val NOT_SET_ROUND_ID: RoundId = 0

        fun areEqual(rounds1: List<UiRound>?, rounds2: List<UiRound>?): Boolean {
            if (rounds1 == null && rounds2 == null) {
                return true
            } else if (rounds1 != null && rounds2 != null) {
                if (rounds1.size != rounds2.size) {
                    return false
                } else {
                    for (i in rounds1.indices) {
                        if (!areEqual(rounds1[i], rounds2[i])) {
                            return false
                        }
                    }
                    return true
                }
            } else {
                return false
            }
        }

        private fun areEqual(round1: UiRound, round2: UiRound): Boolean =
            round1.gameId == round2.gameId &&
                    round1.roundId == round2.roundId &&
                    round1.handPoints == round2.handPoints &&
                    round1.winnerInitialSeat === round2.winnerInitialSeat &&
                    round1.discarderInitialSeat === round2.discarderInitialSeat &&
                    round1.penaltyP1 == round2.penaltyP1 &&
                    round1.penaltyP2 == round2.penaltyP2 &&
                    round1.penaltyP3 == round2.penaltyP3 &&
                    round1.penaltyP4 == round2.penaltyP4 &&
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
