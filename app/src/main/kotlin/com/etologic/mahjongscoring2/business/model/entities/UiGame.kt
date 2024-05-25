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
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import java.util.Date

typealias GameId = Long

data class UiGame(
    val gameId: GameId,
    val nameP1: String,
    val nameP2: String,
    val nameP3: String,
    val nameP4: String,
    val startDate: Date,
    val endDate: Date?,
    val gameName: String,
    val uiRounds: List<UiRound>,
) {
    val ongoingRound: UiRound = uiRounds.last()
    val isEnded: Boolean = endDate != null
    val playersNames: Array<String> = arrayOf(nameP1, nameP2, nameP3, nameP4)

    constructor() : this(
        gameId = NOT_SET_GAME_ID,
        nameP1 = "",
        nameP2 = "",
        nameP3 = "",
        nameP4 = "",
        startDate = Date(),
        endDate = null,
        gameName = "",
        uiRounds = listOf(UiRound()),
    )

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
        }

        uiRounds.forEach { uiRound ->
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
        with(uiRound) {
            when (winnerInitialSeat) {
                null,
                NONE -> intArrayOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)

                else -> if (discarderInitialSeat == null || discarderInitialSeat == NONE) {
                    intArrayOf(
                        penaltyP1 + calculateSelfPickSeatPoints(EAST, winnerInitialSeat, handPoints),
                        penaltyP2 + calculateSelfPickSeatPoints(SOUTH, winnerInitialSeat, handPoints),
                        penaltyP3 + calculateSelfPickSeatPoints(WEST, winnerInitialSeat, handPoints),
                        penaltyP4 + calculateSelfPickSeatPoints(NORTH, winnerInitialSeat, handPoints),
                    )
                } else {
                    intArrayOf(
                        penaltyP1 + calculateDiscardSeatPoints(EAST, winnerInitialSeat, discarderInitialSeat, handPoints),
                        penaltyP2 + calculateDiscardSeatPoints(SOUTH, winnerInitialSeat, discarderInitialSeat, handPoints),
                        penaltyP3 + calculateDiscardSeatPoints(WEST, winnerInitialSeat, discarderInitialSeat, handPoints),
                        penaltyP4 + calculateDiscardSeatPoints(NORTH, winnerInitialSeat, discarderInitialSeat, handPoints),
                    )
                }
            }
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

    private fun calculateSelfPickSeatPoints(
        seat: TableWinds,
        winnerInitialSeat: TableWinds,
        handPoints: Int,
    ): Int =
        if (seat == winnerInitialSeat) {
            getHuSelfPickWinnerPoints(handPoints)
        } else {
            getHuSelfPickDiscarderPoints(handPoints)
        }

    private fun calculateDiscardSeatPoints(
        seat: TableWinds,
        winnerInitialSeat: TableWinds,
        discarderInitialSeat: TableWinds,
        handPoints: Int,
    ): Int =
        when (seat) {
            winnerInitialSeat -> getHuDiscardWinnerPoints(handPoints)
            discarderInitialSeat -> getHuDiscardDiscarderPoints(handPoints)
            else -> POINTS_DISCARD_NEUTRAL_PLAYERS
        }

    fun getBestHand(): BestHand {
        var bestHand = BestHand()
        this.uiRounds
            .filter { it.winnerInitialSeat != null }
            .forEach { uiRound ->
                if (uiRound.handPoints > bestHand.handValue) {
                    bestHand = BestHand(
                        roundNumber = uiRound.roundNumber,
                        handValue = uiRound.handPoints,
                        playerInitialPosition = uiRound.winnerInitialSeat!!,
                        playerName = getPlayerNameByInitialPosition(uiRound.winnerInitialSeat)
                    )
                }
            }
        return bestHand
    }

    fun getPlayerNameByInitialPosition(initialPosition: TableWinds): String {
        return when (initialPosition) {
            NONE -> ""
            EAST -> nameP1
            SOUTH -> nameP2
            WEST -> nameP3
            NORTH -> nameP4
        }
    }

    fun getCurrentEastSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentSeat()
        return when (ongoingRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[EAST.code]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[SOUTH.code]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[WEST.code]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[NORTH.code]
            else -> null
        }
    }

    fun getCurrentSouthSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentSeat()
        return when (ongoingRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[SOUTH.code]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[WEST.code]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[NORTH.code]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[EAST.code]
            else -> null
        }
    }

    fun getCurrentWestSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentSeat()
        return when (ongoingRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[WEST.code]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[NORTH.code]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[EAST.code]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[SOUTH.code]
            else -> null
        }
    }

    fun getCurrentNorthSeatPlayerName(): String? {
        val playersNamesByCurrentRoundSeat = getPlayersNamesByCurrentSeat()
        return when (ongoingRound.roundNumber) {
            1, 5, 9, 13 -> playersNamesByCurrentRoundSeat[NORTH.code]
            2, 6, 10, 14 -> playersNamesByCurrentRoundSeat[EAST.code]
            3, 7, 11, 15 -> playersNamesByCurrentRoundSeat[SOUTH.code]
            4, 8, 12, 16 -> playersNamesByCurrentRoundSeat[WEST.code]
            else -> null
        }
    }

    fun getPlayersNamesByCurrentSeat(): Array<String> {
        val namesListByCurrentSeat = arrayOf("", "", "", "")
        val currentRoundNumber = ongoingRound.roundNumber
        namesListByCurrentSeat[getInitialEastPlayerCurrentSeat(currentRoundNumber).code] = nameP1
        namesListByCurrentSeat[getInitialSouthPlayerCurrentSeat(currentRoundNumber).code] = nameP2
        namesListByCurrentSeat[getInitialWestPlayerCurrentSeat(currentRoundNumber).code] = nameP3
        namesListByCurrentSeat[getInitialNorthPlayerCurrentSeat(currentRoundNumber).code] = nameP4
        return namesListByCurrentSeat
    }

    fun getPlayersTotalPointsByCurrentSeat(): IntArray {
        val totalPointsByInitialSeat = intArrayOf(
            ongoingRound.totalPointsP1,
            ongoingRound.totalPointsP2,
            ongoingRound.totalPointsP3,
            ongoingRound.totalPointsP4,
        )
        return intArrayOf(
            totalPointsByInitialSeat[getPlayerInitialSeatByCurrentSeat(EAST).code],
            totalPointsByInitialSeat[getPlayerInitialSeatByCurrentSeat(SOUTH).code],
            totalPointsByInitialSeat[getPlayerInitialSeatByCurrentSeat(WEST).code],
            totalPointsByInitialSeat[getPlayerInitialSeatByCurrentSeat(NORTH).code],
        )
    }

    fun getPlayersPenaltiesByCurrentSeat(): IntArray {
        val penaltiesByInitialSeat = intArrayOf(
            ongoingRound.penaltyP1,
            ongoingRound.penaltyP2,
            ongoingRound.penaltyP3,
            ongoingRound.penaltyP4,
        )
        return intArrayOf(
            penaltiesByInitialSeat[getPlayerInitialSeatByCurrentSeat(EAST).code],
            penaltiesByInitialSeat[getPlayerInitialSeatByCurrentSeat(SOUTH).code],
            penaltiesByInitialSeat[getPlayerInitialSeatByCurrentSeat(WEST).code],
            penaltiesByInitialSeat[getPlayerInitialSeatByCurrentSeat(NORTH).code],
        )
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

    fun getPlayerInitialSeatByCurrentSeat(currentSeatPosition: TableWinds): TableWinds =
        when (ongoingRound.roundNumber) {
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

    val finishedRounds: List<UiRound>
        get() = uiRounds.filter { !it.isOngoing() }

    companion object {

        const val NOT_SET_GAME_ID: Long = 0
        const val NUM_MCR_PLAYERS = 4
        const val MAX_MCR_ROUNDS = 16
        const val MIN_MCR_POINTS = 8
        const val MAX_MCR_POINTS = 9999
        const val NUM_NO_WINNER_PLAYERS = 3
        const val POINTS_DISCARD_NEUTRAL_PLAYERS = -8

        fun getHuSelfPickWinnerPoints(huPoints: Int) =
            (huPoints + MIN_MCR_POINTS) * NUM_NO_WINNER_PLAYERS

        fun getHuSelfPickDiscarderPoints(huPoints: Int) =
            -(huPoints + MIN_MCR_POINTS)

        fun getHuDiscardWinnerPoints(huPoints: Int) =
            huPoints + (MIN_MCR_POINTS * NUM_NO_WINNER_PLAYERS)

        fun getHuDiscardDiscarderPoints(huPoints: Int) =
            -(huPoints + MIN_MCR_POINTS)

        fun getPenaltyOtherPlayersPoints(penaltyPoints: Int) =
            penaltyPoints / NUM_NO_WINNER_PLAYERS
    }
}
