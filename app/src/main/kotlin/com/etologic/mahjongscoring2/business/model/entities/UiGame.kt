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
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import java.util.Date
import java.util.Locale

data class UiGame(
    val dbGame: DbGame,
    val uiRounds: List<UiRound>,
) {
    constructor() : this(DbGame(gameId = -1, startDate = Date()), listOf(UiRound()))

    val currentRound: UiRound get() = uiRounds.last()

    init {
        uiRounds.forEachIndexed { index, uiRound ->
            val roundNumber = index + 1
            uiRound.roundNumber = roundNumber
        }

        val bestHand = getBestHand()

        uiRounds.forEach { uiRound ->
            val roundPoints = getRoundPoints(uiRound)
            uiRound.pointsP1 = roundPoints[0]
            uiRound.pointsP2 = roundPoints[1]
            uiRound.pointsP3 = roundPoints[2]
            uiRound.pointsP4 = roundPoints[3]

            val roundTotals = getTotalPointsUntilRound(uiRound.roundNumber)
            uiRound.totalPointsP1 = roundTotals[0]
            uiRound.totalPointsP2 = roundTotals[1]
            uiRound.totalPointsP3 = roundTotals[2]
            uiRound.totalPointsP4 = roundTotals[3]

            val isBestHand = uiRound.roundNumber == bestHand.roundNumber
            uiRound.isBestHand = isBestHand
        }
    }

    private fun getRoundPoints(uiRound: UiRound): IntArray =
        when (uiRound.dbRound.winnerInitialSeat) {
            null,
            TableWinds.NONE -> intArrayOf(0, 0, 0, 0)

            else -> if (uiRound.dbRound.discarderInitialSeat == null ||
                uiRound.dbRound.discarderInitialSeat == TableWinds.NONE
            ) {
                intArrayOf(
                    calculateSelfPickSeatPoints(EAST, uiRound.dbRound),
                    calculateSelfPickSeatPoints(SOUTH, uiRound.dbRound),
                    calculateSelfPickSeatPoints(WEST, uiRound.dbRound),
                    calculateSelfPickSeatPoints(NORTH, uiRound.dbRound),
                )
            } else {
                intArrayOf(
                    calculateDiscardSeatPoints(EAST, uiRound.dbRound),
                    calculateDiscardSeatPoints(SOUTH, uiRound.dbRound),
                    calculateDiscardSeatPoints(WEST, uiRound.dbRound),
                    calculateDiscardSeatPoints(NORTH, uiRound.dbRound),
                )
            }
        }

    private fun calculateSelfPickSeatPoints(seat: TableWinds, dbRound: DbRound): Int =
        if (seat == dbRound.winnerInitialSeat) {
            getHuSelfPickWinnerPoints(dbRound.handPoints)
        } else {
            getHuSelfPickDiscarderPoints(dbRound.handPoints)
        }

    private fun calculateDiscardSeatPoints(seat: TableWinds, dbRound: DbRound): Int =
        when (seat) {
            dbRound.winnerInitialSeat -> getHuDiscardWinnerPoints(dbRound.handPoints)
            dbRound.discarderInitialSeat -> getHuDiscardDiscarderPoints(dbRound.handPoints)
            else -> POINTS_DISCARD_NEUTRAL_PLAYERS
        }

    fun getBestHand(): BestHand {
        var bestHand = BestHand()
        this.uiRounds
            .filter { it.dbRound.winnerInitialSeat != null }
            .forEach { uiRound ->
                if (uiRound.dbRound.handPoints > bestHand.handValue) {
                    bestHand = BestHand(
                        roundNumber = uiRound.roundNumber,
                        handValue = uiRound.dbRound.handPoints,
                        playerInitialPosition = uiRound.dbRound.winnerInitialSeat!!,
                        playerName = dbGame.getPlayerNameByInitialPosition(uiRound.dbRound.winnerInitialSeat!!)
                    )
                }
            }
        return bestHand
    }

    private fun getTotalPointsUntilRound(roundNumber: Int): IntArray {
        val totalPoints = intArrayOf(0, 0, 0, 0)
        for (i in 0..<roundNumber) {
            totalPoints[0] += uiRounds[i].pointsP1
            totalPoints[1] += uiRounds[i].pointsP2
            totalPoints[2] += uiRounds[i].pointsP3
            totalPoints[3] += uiRounds[i].pointsP4
        }
        return totalPoints
    }

    fun getCurrentEastSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentRoundSeat()
        return when (currentRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[EAST.code]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[SOUTH.code]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[WEST.code]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[NORTH.code]
            else -> null
        }
    }

    fun getCurrentSouthSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentRoundSeat()
        return when (currentRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[SOUTH.code]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[WEST.code]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[NORTH.code]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[EAST.code]
            else -> null
        }
    }

    fun getCurrentWestSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentRoundSeat()
        return when (currentRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[WEST.code]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[NORTH.code]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[EAST.code]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[SOUTH.code]
            else -> null
        }
    }

    fun getCurrentNorthSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentRoundSeat()
        return when (currentRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[NORTH.code]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[EAST.code]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[SOUTH.code]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[WEST.code]
            else -> null
        }
    }

    fun getPlayersNamesByCurrentRoundSeat(): Array<String> {
        val namesListByCurrentSeat = arrayOf("", "", "", "")
        val currentRoundNumber = currentRound.roundNumber
        namesListByCurrentSeat[getInitialEastPlayerCurrentSeat(currentRoundNumber).code] =
            dbGame.nameP1
        namesListByCurrentSeat[getInitialSouthPlayerCurrentSeat(currentRoundNumber).code] =
            dbGame.nameP2
        namesListByCurrentSeat[getInitialWestPlayerCurrentSeat(currentRoundNumber).code] =
            dbGame.nameP3
        namesListByCurrentSeat[getInitialNorthPlayerCurrentSeat(currentRoundNumber).code] =
            dbGame.nameP4
        return namesListByCurrentSeat
    }

    fun getPlayersTotalPointsByCurrentSeat(): IntArray {
        val points = getPlayersTotalPointsWithPenalties()
        val pointsByCurrentSeat = intArrayOf(0, 0, 0, 0)
        val currentRoundNumber = currentRound.roundNumber
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(currentRoundNumber).code] =
            points[EAST.code]
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(currentRoundNumber).code] =
            points[SOUTH.code]
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(currentRoundNumber).code] =
            points[WEST.code]
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(currentRoundNumber).code] =
            points[NORTH.code]
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
        val currentRound = currentRound
        val currentRoundNumber = currentRound.roundNumber
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(currentRoundNumber).code] =
            currentRound.dbRound.penaltyP1
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(currentRoundNumber).code] =
            currentRound.dbRound.penaltyP2
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(currentRoundNumber).code] =
            currentRound.dbRound.penaltyP3
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(currentRoundNumber).code] =
            currentRound.dbRound.penaltyP4
        return pointsByCurrentSeat
    }

    private fun getInitialEastPlayerCurrentSeat(roundNumber: Int): TableWinds =
        when (roundNumber) {
            1, 2, 3, 4 -> EAST
            5, 6, 7, 8 -> SOUTH
            9, 10, 11, 12 -> NORTH
            13, 14, 15, 16 -> WEST
            else -> EAST
        }

    private fun getInitialSouthPlayerCurrentSeat(roundNumber: Int): TableWinds =
        when (roundNumber) {
            1, 2, 3, 4 -> SOUTH
            5, 6, 7, 8 -> EAST
            9, 10, 11, 12 -> WEST
            13, 14, 15, 16 -> NORTH
            else -> SOUTH
        }

    private fun getInitialWestPlayerCurrentSeat(roundNumber: Int): TableWinds =
        when (roundNumber) {
            1, 2, 3, 4 -> WEST
            5, 6, 7, 8 -> NORTH
            9, 10, 11, 12 -> EAST
            13, 14, 15, 16 -> SOUTH
            else -> WEST
        }

    private fun getInitialNorthPlayerCurrentSeat(roundNumber: Int): TableWinds =
        when (roundNumber) {
            1, 2, 3, 4 -> NORTH
            5, 6, 7, 8 -> WEST
            9, 10, 11, 12 -> SOUTH
            13, 14, 15, 16 -> EAST
            else -> NORTH
        }

    fun getPlayersTotalPenaltiesStringSigned(): Array<String>? =
        if (uiRounds.all { it.dbRound.penaltyP1 == 0 && it.dbRound.penaltyP2 == 0 && it.dbRound.penaltyP3 == 0 && it.dbRound.penaltyP4 == 0 }) {
            null
        } else {
            val penalties = getPlayersTotalPenalties()
            arrayOf(
                String.format(Locale.getDefault(), "%+d", penalties[EAST.code]),
                String.format(Locale.getDefault(), "%+d", penalties[SOUTH.code]),
                String.format(Locale.getDefault(), "%+d", penalties[WEST.code]),
                String.format(Locale.getDefault(), "%+d", penalties[NORTH.code])
            )
        }

    private fun getPlayersTotalPenalties(): IntArray {
        val points = intArrayOf(0, 0, 0, 0)
        uiRounds.forEach {
            points[EAST.code] += it.dbRound.penaltyP1
            points[SOUTH.code] += it.dbRound.penaltyP2
            points[WEST.code] += it.dbRound.penaltyP3
            points[NORTH.code] += it.dbRound.penaltyP4
        }
        return points
    }

    fun getPlayersTotalPointsWithPenaltiesStringSigned(): Array<String> {
        val points = getPlayersTotalPointsWithPenalties()
        return arrayOf(
            String.format(Locale.getDefault(), "%+d", points[EAST.code]),
            String.format(Locale.getDefault(), "%+d", points[SOUTH.code]),
            String.format(Locale.getDefault(), "%+d", points[WEST.code]),
            String.format(Locale.getDefault(), "%+d", points[NORTH.code])
        )
    }

    fun getPlayersTotalPointsWithPenalties(): IntArray {
        val points = intArrayOf(0, 0, 0, 0)
        uiRounds.forEach {
            points[EAST.code] += it.pointsP1 + it.dbRound.penaltyP1
            points[SOUTH.code] += it.pointsP2 + it.dbRound.penaltyP2
            points[WEST.code] += it.pointsP3 + it.dbRound.penaltyP3
            points[NORTH.code] += it.pointsP4 + it.dbRound.penaltyP4
        }
        return points
    }

    fun getPlayerInitialSeatByCurrentSeat(currentSeatPosition: TableWinds): TableWinds =
        when (currentRound.roundNumber) {
            1, 2, 3, 4 -> getPlayerInitialPositionBySeatInRoundEast(currentSeatPosition)
            5, 6, 7, 8 -> getPlayerInitialPositionBySeatInRoundSouth(currentSeatPosition)
            9, 10, 11, 12 -> getPlayerInitialPositionBySeatInRoundWest(currentSeatPosition)
            13, 14, 15, 16 -> getPlayerInitialPositionBySeatInRoundNorth(currentSeatPosition)
            else -> getPlayerInitialPositionBySeatInRoundNorth(currentSeatPosition)
        }

    private fun getPlayerInitialPositionBySeatInRoundEast(seatPosition: TableWinds): TableWinds =
        when (seatPosition) {
            EAST -> EAST
            SOUTH -> SOUTH
            WEST -> WEST
            NORTH -> NORTH
            else -> NORTH
        }

    private fun getPlayerInitialPositionBySeatInRoundSouth(seatPosition: TableWinds): TableWinds =
        when (seatPosition) {
            EAST -> SOUTH
            SOUTH -> EAST
            WEST -> NORTH
            NORTH -> WEST
            else -> WEST
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

    private fun getPlayerInitialPositionBySeatInRoundNorth(seatPosition: TableWinds): TableWinds =
        when (seatPosition) {
            EAST -> NORTH
            SOUTH -> WEST
            WEST -> EAST
            NORTH -> SOUTH
            else -> SOUTH
        }

    fun getSeatsCurrentWind(roundNumber: Int): Array<TableWinds> =
        when (roundNumber) {
            1, 5, 9, 13 -> arrayOf(EAST, SOUTH, WEST, NORTH)
            2, 6, 10, 14 -> arrayOf(NORTH, EAST, SOUTH, WEST)
            3, 7, 11, 15 -> arrayOf(WEST, NORTH, EAST, SOUTH)
            4, 8, 12, 16 -> arrayOf(SOUTH, WEST, NORTH, EAST)
            else -> arrayOf()
        }

    fun getEndedRounds(): List<UiRound> =
        uiRounds.takeIf { dbGame.isEnded } ?: uiRounds.dropLast(1)

    companion object {

        const val NUM_MCR_PLAYERS = 4
        const val MAX_MCR_ROUNDS = 16
        const val MIN_MCR_POINTS = 8
        const val MAX_MCR_POINTS = 9999
        const val NUM_NO_WINNER_PLAYERS = 3
        const val POINTS_DISCARD_NEUTRAL_PLAYERS = -8

        fun getHuSelfPickWinnerPoints(huPoints: Int) =
            (huPoints + MIN_MCR_POINTS) * NUM_NO_WINNER_PLAYERS

        fun getHuSelfPickDiscarderPoints(huPoints: Int) = -(huPoints + MIN_MCR_POINTS)

        fun getHuDiscardWinnerPoints(huPoints: Int) =
            huPoints + (MIN_MCR_POINTS * NUM_NO_WINNER_PLAYERS)

        fun getHuDiscardDiscarderPoints(huPoints: Int) = -(huPoints + MIN_MCR_POINTS)

        fun getPenaltyOtherPlayersPoints(penaltyPoints: Int) = penaltyPoints / NUM_NO_WINNER_PLAYERS
    }
}