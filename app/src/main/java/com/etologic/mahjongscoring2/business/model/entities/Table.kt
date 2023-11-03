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

import androidx.room.Embedded
import androidx.room.Relation
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.TableDiffs
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST

class Table(@field:Embedded var game: Game) {

    @Relation(parentColumn = "gameId", entityColumn = "gameId")
    var rounds: List<Round> = ArrayList()

    internal fun initBestHandAndTotalsAndRoundNumbers(): Table {
        setBestHand()
        setTotals()
        setRoundNumbers()
        return this
    }

    private fun setBestHand() {
        val bestHand = getBestHand()
        if (bestHand.handValue >= MIN_MCR_POINTS) {
            for (round in rounds) {
                val isBestHandRound = round.handPoints >= bestHand.handValue && round.winnerInitialSeat == bestHand.playerInitialPosition
                round.isBestHand = isBestHandRound
            }
        }
    }

    private fun setTotals() {
        rounds.forEachIndexed { index, round ->
            if (round.isEnded) {
                val newRoundTotals = getTotalPointsUntilRound(index)
                round.totalPointsP1 = newRoundTotals[0]
                round.totalPointsP2 = newRoundTotals[1]
                round.totalPointsP3 = newRoundTotals[2]
                round.totalPointsP4 = newRoundTotals[3]
            }
        }
    }

    private fun getTotalPointsUntilRound(index: Int): IntArray {
        val totalPoints = intArrayOf(0, 0, 0, 0)
        for (i in 0..index) {
            totalPoints[0] += rounds[i].pointsP1
            totalPoints[1] += rounds[i].pointsP2
            totalPoints[2] += rounds[i].pointsP3
            totalPoints[3] += rounds[i].pointsP4
        }
        return totalPoints
    }

    private fun setRoundNumbers() {
        rounds.forEachIndexed { index, round -> round.roundNumber = index + 1 }
    }

    internal fun getCopy(): Table {
        val gameWithRounds = Table(game.copy)
        val newRounds = ArrayList<Round>(rounds.size)
        rounds.map { newRounds.add(it.getCopy()) }
        gameWithRounds.rounds = newRounds
        return gameWithRounds
    }

    internal fun getPlayersNamesByCurrentSeat(): Array<String> {
        val namesListByCurrentSeat = arrayOf("", "", "", "")
        val roundId = rounds.size
        namesListByCurrentSeat[getInitialEastPlayerCurrentSeat(roundId).code] = game.nameP1
        namesListByCurrentSeat[getInitialSouthPlayerCurrentSeat(roundId).code] = game.nameP2
        namesListByCurrentSeat[getInitialWestPlayerCurrentSeat(roundId).code] = game.nameP3
        namesListByCurrentSeat[getInitialNorthPlayerCurrentSeat(roundId).code] = game.nameP4
        return namesListByCurrentSeat
    }

    internal fun getPlayersTotalPointsByCurrentSeat(): IntArray {
        val points = getPlayersTotalPoints()
        val pointsByCurrentSeat = intArrayOf(0, 0, 0, 0)
        val roundId = rounds.size
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(roundId).code] = points[EAST.code]
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(roundId).code] = points[SOUTH.code]
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(roundId).code] = points[WEST.code]
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(roundId).code] = points[NORTH.code]
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

    internal fun getPlayersPenaltiesByCurrentSeat(): IntArray {
        val pointsByCurrentSeat = intArrayOf(0, 0, 0, 0)
        val currentRound = rounds.last()
        val roundId = rounds.size
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(roundId).code] = currentRound.penaltyP1
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(roundId).code] = currentRound.penaltyP2
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(roundId).code] = currentRound.penaltyP3
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(roundId).code] = currentRound.penaltyP4
        return pointsByCurrentSeat
    }

    private fun getInitialEastPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> EAST
            5, 6, 7, 8 -> SOUTH
            9, 10, 11, 12 -> NORTH
            13, 14, 15, 16 -> WEST
            else -> EAST
        }
    }

    private fun getInitialSouthPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> SOUTH
            5, 6, 7, 8 -> EAST
            9, 10, 11, 12 -> WEST
            13, 14, 15, 16 -> NORTH
            else -> SOUTH
        }
    }

    private fun getInitialWestPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> WEST
            5, 6, 7, 8 -> NORTH
            9, 10, 11, 12 -> EAST
            13, 14, 15, 16 -> SOUTH
            else -> WEST
        }
    }

    private fun getInitialNorthPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> NORTH
            5, 6, 7, 8 -> WEST
            9, 10, 11, 12 -> SOUTH
            13, 14, 15, 16 -> EAST
            else -> NORTH
        }
    }

    internal fun getPlayersTotalPointsStringSigned(): Array<String> {
        val points = getPlayersTotalPoints()
        return arrayOf(
            String.format("%+d", points[EAST.code]),
            String.format("%+d", points[SOUTH.code]),
            String.format("%+d", points[WEST.code]),
            String.format("%+d", points[NORTH.code])
        )
    }

    internal fun getPlayersTotalPoints(): IntArray {
        val points = intArrayOf(0, 0, 0, 0)
        rounds.forEach {
            points[EAST.code] += it.pointsP1
            points[SOUTH.code] += it.pointsP2
            points[WEST.code] += it.pointsP3
            points[NORTH.code] += it.pointsP4
        }
        return points
    }

    internal fun getEndedRounds(): List<Round> {
        return getCopyWithoutLastRoundIfNotEnded()
    }

    private fun getCopyWithoutLastRoundIfNotEnded(): List<Round> {
        if (!rounds.last().isEnded) {
            val mutableRounds = rounds.toMutableList()
            mutableRounds.removeAt(rounds.size - 1)
            return mutableRounds
        }
        return rounds
    }

    internal fun getBestHand(): BestHand {
        val bestHand = BestHand()
        for (round in rounds) {
            if (round.handPoints > bestHand.handValue) {
                bestHand.handValue = round.handPoints
                bestHand.playerName = game.getPlayerNameByInitialPosition(round.winnerInitialSeat)
            }
        }
        return bestHand
    }

    internal fun getPlayerInitialSeatByCurrentSeat(currentSeatPosition: TableWinds, roundId: Int): TableWinds {
        return when (roundId) {
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

    internal fun getSeatsCurrentWind(roundId: Int): Array<TableWinds> {
        return when (roundId) {
            1, 5, 9, 13 -> arrayOf(EAST, SOUTH, WEST, NORTH)
            2, 6, 10, 14 -> arrayOf(NORTH, EAST, SOUTH, WEST)
            3, 7, 11, 15 -> arrayOf(WEST, NORTH, EAST, SOUTH)
            4, 8, 12, 16 -> arrayOf(SOUTH, WEST, NORTH, EAST)
            else -> arrayOf()
        }
    }

    internal fun finishCurrentRoundByHuDiscard(huData: HuData): Table {
        val currentRound = rounds.last()
        currentRound.finishRoundByHuDiscard(
            getPlayerInitialSeatByCurrentSeat(huData.winnerCurrentSeat, rounds.size),
            getPlayerInitialSeatByCurrentSeat(huData.discarderCurrentSeat!!, rounds.size),
            huData.points
        )
        currentRound.setTotalsPoints(getPlayersTotalPoints())
        return this
    }

    internal fun finishCurrentRoundByHuSelfPick(huData: HuData): Table {
        val currentRound = rounds.last()
        currentRound.finishRoundByHuSelfPick(
            getPlayerInitialSeatByCurrentSeat(huData.winnerCurrentSeat, rounds.size),
            huData.points
        )
        currentRound.setTotalsPoints(getPlayersTotalPoints())
        return this
    }

    internal fun finishCurrentRoundByDraw(): Table {
        val currentRound = rounds.last()
        currentRound.finishRoundByDraw()
        currentRound.setTotalsPoints(getPlayersTotalPoints())
        return this
    }

    internal companion object {

        internal const val NUM_MCR_PLAYERS = 4
        internal const val MAX_MCR_ROUNDS = 16
        internal const val MIN_MCR_POINTS = 8
        internal const val MAX_MCR_POINTS = 9999
        internal const val NUM_NO_WINNER_PLAYERS = 3
        internal const val POINTS_DISCARD_NEUTRAL_PLAYERS = -8

        internal fun getHuSelfPickWinnerPoints(huPoints: Int) = (huPoints + MIN_MCR_POINTS) * NUM_NO_WINNER_PLAYERS

        internal fun getHuSelfPickDiscarderPoints(huPoints: Int) = -(huPoints + MIN_MCR_POINTS)

        internal fun getHuDiscardWinnerPoints(huPoints: Int) = huPoints + (MIN_MCR_POINTS * NUM_NO_WINNER_PLAYERS)

        internal fun getHuDiscardDiscarderPoints(huPoints: Int) = -(huPoints + MIN_MCR_POINTS)

        internal fun getPenaltyOtherPlayersPoints(penaltyPoints: Int) = penaltyPoints / NUM_NO_WINNER_PLAYERS
    }
}
