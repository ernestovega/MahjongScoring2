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

import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.dtos.TableDiffs
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.data_source.model.DBGame

data class UIGame(
    val dbGame: DBGame,
    val rounds: List<Round>,
) {
    init {
        fun Round.setRoundNumber(roundNumber: Int) {
            this.roundNumber = roundNumber
        }

        fun Round.setTotals(roundNumber: Int) {
            fun UIGame.getTotalPointsUntilRound(roundNumber: Int): IntArray {
                val totalPoints = intArrayOf(0, 0, 0, 0)
                for (i in 0..<roundNumber) {
                    totalPoints[0] += rounds[i].pointsP1
                    totalPoints[1] += rounds[i].pointsP2
                    totalPoints[2] += rounds[i].pointsP3
                    totalPoints[3] += rounds[i].pointsP4
                }
                return totalPoints
            }

            if (this.isEnded) {
                val newRoundTotals = getTotalPointsUntilRound(roundNumber)
                this.totalPointsP1 = newRoundTotals[0]
                this.totalPointsP2 = newRoundTotals[1]
                this.totalPointsP3 = newRoundTotals[2]
                this.totalPointsP4 = newRoundTotals[3]
            }
        }

        fun Round.setBestHand(bestHand: BestHand) {
            this.isBestHand = this.handPoints >= bestHand.handValue &&
                    this.winnerInitialSeat == bestHand.playerInitialPosition
        }

        val bestHand = getBestHand()
        rounds.forEachIndexed { index, round ->
            val roundNumber = index + 1
            round.setRoundNumber(roundNumber)
            round.setTotals(roundNumber)
            round.setBestHand(bestHand)
        }
    }

    fun getBestHand(): BestHand {
        var bestHand = BestHand()
        this.rounds.forEach { round ->
            if (round.handPoints > bestHand.handValue) {
                bestHand = BestHand(
                    handValue = round.handPoints,
                    playerInitialPosition = round.winnerInitialSeat,
                    playerName = dbGame.getPlayerNameByInitialPosition(round.winnerInitialSeat)
                )
            }
        }
        return bestHand
    }

    fun getPlayersNamesByCurrentSeat(): Array<String> {
        val namesListByCurrentSeat = arrayOf("", "", "", "")
        val currentRoundNumber = rounds.lastOrNull()?.roundNumber ?: 1
        namesListByCurrentSeat[getInitialEastPlayerCurrentSeat(currentRoundNumber).code] = dbGame.nameP1
        namesListByCurrentSeat[getInitialSouthPlayerCurrentSeat(currentRoundNumber).code] = dbGame.nameP2
        namesListByCurrentSeat[getInitialWestPlayerCurrentSeat(currentRoundNumber).code] = dbGame.nameP3
        namesListByCurrentSeat[getInitialNorthPlayerCurrentSeat(currentRoundNumber).code] = dbGame.nameP4
        return namesListByCurrentSeat
    }

    fun getPlayersTotalPointsByCurrentSeat(): IntArray {
        val points = getPlayersTotalPoints()
        val pointsByCurrentSeat = intArrayOf(0, 0, 0, 0)
        val currentRoundNumber = rounds.lastOrNull()?.roundNumber ?: 1
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(currentRoundNumber).code] = points[EAST.code]
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(currentRoundNumber).code] = points[SOUTH.code]
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(currentRoundNumber).code] = points[WEST.code]
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(currentRoundNumber).code] = points[NORTH.code]
        return pointsByCurrentSeat
    }

    fun getTableDiffs(): TableDiffs =
        with(getPlayersTotalPointsByCurrentSeat()) {
            TableDiffs(
                eastSeatPoints = this[EAST.code],
                southSeatPoints = this[SOUTH.code],
                westSeatPoints = this[WEST.code],
                northSeatPoints = this[NORTH.code],
            )
        }

    fun getPlayersPenaltiesByCurrentSeat(): IntArray {
        val pointsByCurrentSeat = intArrayOf(0, 0, 0, 0)
        val currentRound = rounds.lastOrNull()
        val currentRoundNumber = rounds.lastOrNull()?.roundNumber ?: 1
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(currentRoundNumber).code] = currentRound?.penaltyP1 ?: 0
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(currentRoundNumber).code] = currentRound?.penaltyP2 ?: 0
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(currentRoundNumber).code] = currentRound?.penaltyP3 ?: 0
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(currentRoundNumber).code] = currentRound?.penaltyP4 ?: 0
        return pointsByCurrentSeat
    }

    private fun getInitialEastPlayerCurrentSeat(roundNumber: Int): TableWinds {
        return when (roundNumber) {
            1, 2, 3, 4 -> EAST
            5, 6, 7, 8 -> SOUTH
            9, 10, 11, 12 -> NORTH
            13, 14, 15, 16 -> WEST
            else -> EAST
        }
    }

    private fun getInitialSouthPlayerCurrentSeat(roundNumber: Int): TableWinds {
        return when (roundNumber) {
            1, 2, 3, 4 -> SOUTH
            5, 6, 7, 8 -> EAST
            9, 10, 11, 12 -> WEST
            13, 14, 15, 16 -> NORTH
            else -> SOUTH
        }
    }

    private fun getInitialWestPlayerCurrentSeat(roundNumber: Int): TableWinds {
        return when (roundNumber) {
            1, 2, 3, 4 -> WEST
            5, 6, 7, 8 -> NORTH
            9, 10, 11, 12 -> EAST
            13, 14, 15, 16 -> SOUTH
            else -> WEST
        }
    }

    private fun getInitialNorthPlayerCurrentSeat(roundNumber: Int): TableWinds {
        return when (roundNumber) {
            1, 2, 3, 4 -> NORTH
            5, 6, 7, 8 -> WEST
            9, 10, 11, 12 -> SOUTH
            13, 14, 15, 16 -> EAST
            else -> NORTH
        }
    }

    fun getPlayersTotalPointsStringSigned(): Array<String> {
        val points = getPlayersTotalPoints()
        return arrayOf(
            String.format("%+d", points[EAST.code]),
            String.format("%+d", points[SOUTH.code]),
            String.format("%+d", points[WEST.code]),
            String.format("%+d", points[NORTH.code])
        )
    }

    fun getPlayersTotalPoints(): IntArray {
        val points = intArrayOf(0, 0, 0, 0)
        rounds.forEach {
            points[EAST.code] += it.pointsP1
            points[SOUTH.code] += it.pointsP2
            points[WEST.code] += it.pointsP3
            points[NORTH.code] += it.pointsP4
        }
        return points
    }

    fun getEndedRounds(): List<Round> =
        if (rounds.lastOrNull()?.isEnded == false) {
            rounds.toMutableList().apply { removeAt(rounds.size - 1) }
        } else {
            rounds
        }

    fun getPlayerInitialSeatByCurrentSeat(currentSeatPosition: TableWinds): TableWinds {
        return when (rounds.lastOrNull()?.roundNumber ?: 1) {
            1, 2, 3, 4 -> getPlayerInitialPositionBySeatInRoundEast(currentSeatPosition)
            5, 6, 7, 8 -> getPlayerInitialPositionBySeatInRoundSouth(currentSeatPosition)
            9, 10, 11, 12 -> getPlayerInitialPositionBySeatInRoundWest(currentSeatPosition)
            13, 14, 15, 16 -> getPlayerInitialPositionBySeatInRoundNorth(currentSeatPosition)
            else -> getPlayerInitialPositionBySeatInRoundNorth(currentSeatPosition)
        }
    }

    private fun getPlayerInitialPositionBySeatInRoundEast(seatPosition: TableWinds): TableWinds {
        return when (seatPosition) {
            EAST -> EAST
            SOUTH -> SOUTH
            WEST -> WEST
            NORTH -> NORTH
            else -> NORTH
        }
    }

    private fun getPlayerInitialPositionBySeatInRoundSouth(seatPosition: TableWinds): TableWinds {
        return when (seatPosition) {
            EAST -> SOUTH
            SOUTH -> EAST
            WEST -> NORTH
            NORTH -> WEST
            else -> WEST
        }
    }

    private fun getPlayerInitialPositionBySeatInRoundWest(seatPosition: TableWinds): TableWinds {
        return when (seatPosition) {
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> SOUTH
            NORTH -> EAST
            else -> EAST
        }
    }

    private fun getPlayerInitialPositionBySeatInRoundNorth(seatPosition: TableWinds): TableWinds {
        return when (seatPosition) {
            EAST -> NORTH
            SOUTH -> WEST
            WEST -> EAST
            NORTH -> SOUTH
            else -> SOUTH
        }
    }

    fun getSeatsCurrentWind(roundNumber: Int): Array<TableWinds> {
        return when (roundNumber) {
            1, 5, 9, 13 -> arrayOf(EAST, SOUTH, WEST, NORTH)
            2, 6, 10, 14 -> arrayOf(NORTH, EAST, SOUTH, WEST)
            3, 7, 11, 15 -> arrayOf(WEST, NORTH, EAST, SOUTH)
            4, 8, 12, 16 -> arrayOf(SOUTH, WEST, NORTH, EAST)
            else -> arrayOf()
        }
    }

    companion object {

        const val NUM_MCR_PLAYERS = 4
        const val MAX_MCR_ROUNDS = 16
        const val MIN_MCR_POINTS = 8
        const val MAX_MCR_POINTS = 9999
        const val NUM_NO_WINNER_PLAYERS = 3
        const val POINTS_DISCARD_NEUTRAL_PLAYERS = -8

        fun getHuSelfPickWinnerPoints(huPoints: Int) = (huPoints + MIN_MCR_POINTS) * NUM_NO_WINNER_PLAYERS

        fun getHuSelfPickDiscarderPoints(huPoints: Int) = -(huPoints + MIN_MCR_POINTS)

        fun getHuDiscardWinnerPoints(huPoints: Int) = huPoints + (MIN_MCR_POINTS * NUM_NO_WINNER_PLAYERS)

        fun getHuDiscardDiscarderPoints(huPoints: Int) = -(huPoints + MIN_MCR_POINTS)

        fun getPenaltyOtherPlayersPoints(penaltyPoints: Int) = penaltyPoints / NUM_NO_WINNER_PLAYERS
    }
}
