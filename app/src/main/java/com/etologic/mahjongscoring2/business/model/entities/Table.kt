package com.etologic.mahjongscoring2.business.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import java.util.*

class Table(@field:Embedded var game: Game) {
    
    @Relation(parentColumn = "gameId", entityColumn = "gameId")
    var rounds: List<Round> = ArrayList()
    
    internal fun getCopy(): Table {
        val gameWithRounds = Table(game.copy)
        val newRounds = ArrayList<Round>(rounds.size)
        rounds.map { newRounds.add(it.getCopy()) }
        gameWithRounds.rounds = newRounds
        return gameWithRounds
    }
    
    internal fun getPlayersNamesByCurrentSeat(): Array<String> {
        val namesListByCurrentSeat = arrayOf("", "", "", "")
        val roundId = rounds.last().roundId
        namesListByCurrentSeat[getInitialEastPlayerCurrentSeat(roundId).code] = game.nameP1
        namesListByCurrentSeat[getInitialSouthPlayerCurrentSeat(roundId).code] = game.nameP2
        namesListByCurrentSeat[getInitialWestPlayerCurrentSeat(roundId).code] = game.nameP3
        namesListByCurrentSeat[getInitialNorthPlayerCurrentSeat(roundId).code] = game.nameP4
        return namesListByCurrentSeat
    }
    
    private fun getPlayersTotalPointsByCurrentSeat(): Array<Int> {
        val points = getPlayersTotalPoints()
        val pointsByCurrentSeat = arrayOf(0, 0, 0, 0)
        val roundId = rounds.last().roundId
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(roundId).code] = points[EAST.code]
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(roundId).code] = points[SOUTH.code]
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(roundId).code] = points[WEST.code]
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(roundId).code] = points[NORTH.code]
        return pointsByCurrentSeat
    }
    
    internal fun getPlayersTotalPointsStringByCurrentSeat(): List<String> =
        getPlayersTotalPointsByCurrentSeat().map { String.format(Locale.getDefault(), "%d", it) }
    
    internal fun getPlayersPenaltiesByCurrentSeat(): Array<Int> {
        val pointsByCurrentSeat = arrayOf(0, 0, 0, 0)
        val currentRound = rounds.last()
        val roundId = currentRound.roundId
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(roundId).code] = currentRound.penaltyP1
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(roundId).code] = currentRound.penaltyP2
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(roundId).code] = currentRound.penaltyP3
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(roundId).code] = currentRound.penaltyP4
        return pointsByCurrentSeat
    }
    
    private fun getInitialEastPlayerCurrentSeat(roundId: Int): TableWinds =
        when (roundId) {
            1, 2, 3, 4 -> EAST
            5, 6, 7, 8 -> SOUTH
            9, 10, 11, 12 -> NORTH
            13, 14, 15, 16 -> WEST
            else -> EAST
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
    
    internal fun getPlayersTotalPointsString(): Array<String> {
        val points = getPlayersTotalPoints()
        return arrayOf(
            points[EAST.code].toString(),
            points[SOUTH.code].toString(),
            points[WEST.code].toString(),
            points[NORTH.code].toString()
        )
    }
    
    private fun getPlayersTotalPoints(): IntArray {
        val points = intArrayOf(0, 0, 0, 0)
        for (round in rounds) {
            points[EAST.code] += round.pointsP1
            points[SOUTH.code] += round.pointsP2
            points[WEST.code] += round.pointsP3
            points[NORTH.code] += round.pointsP4
        }
        return points
    }
    
    internal fun getEndedRoundsWithBestHand(): List<Round> {
        val bestHand = getBestHand()
        if (bestHand.handValue >= MIN_MCR_POINTS) {
            for (round in rounds) {
                val isBestHandRound = round.handPoints >= bestHand.handValue && round.winnerInitialSeat == bestHand.playerInitialPosition
                round.isBestHand = isBestHandRound
            }
        }
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
    
    internal fun getDuration(): Long {
        var gameDuration: Long = 0
        for (round in rounds) gameDuration += round.roundDuration
        return gameDuration
    }
    
    internal fun getTotalScoreP1(rounds: List<Round>): Int {
        var totalPoints = 0
        for (round in rounds) totalPoints += round.pointsP1
        return totalPoints
    }
    
    internal fun getTotalScoreP2(rounds: List<Round>): Int {
        var totalPoints = 0
        for (round in rounds) totalPoints += round.pointsP2
        return totalPoints
    }
    
    internal fun getTotalScoreP3(rounds: List<Round>): Int {
        var totalPoints = 0
        for (round in rounds) totalPoints += round.pointsP3
        return totalPoints
    }
    
    internal fun getTotalScoreP4(rounds: List<Round>): Int {
        var totalPoints = 0
        for (round in rounds) totalPoints += round.pointsP4
        return totalPoints
    }
    
    internal fun getPlayerInitialSeatByCurrentSeat(currentSeatPosition: TableWinds, roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> getPlayerInitialPositionBySeatInRoundEast(
                currentSeatPosition
            )
            5, 6, 7, 8 -> getPlayerInitialPositionBySeatInRoundSouth(
                currentSeatPosition
            )
            9, 10, 11, 12 -> getPlayerInitialPositionBySeatInRoundWest(
                currentSeatPosition
            )
            13, 14, 15, 16 -> getPlayerInitialPositionBySeatInRoundNorth(
                currentSeatPosition
            )
            else -> getPlayerInitialPositionBySeatInRoundNorth(
                currentSeatPosition
            )
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
    
    internal fun getSeatsCurrentWind(roundId: Int): Array<TableWinds> =
        when (roundId) {
            1, 5, 9, 13 -> arrayOf(EAST, SOUTH, WEST, NORTH)
            2, 6, 10, 14 -> arrayOf(NORTH, EAST, SOUTH, WEST)
            3, 7, 11, 15 -> arrayOf(WEST, NORTH, EAST, SOUTH)
            4, 8, 12, 16 -> arrayOf(SOUTH, WEST, NORTH, EAST)
            else -> arrayOf()
        }
    
    internal fun finishCurrentRoundByHuDiscard(huData: HuData): Table {
        val currentRound = rounds.last()
        currentRound.finishRoundByHuDiscard(
            getPlayerInitialSeatByCurrentSeat(huData.winnerCurrentSeat, currentRound.roundId),
            getPlayerInitialSeatByCurrentSeat(huData.discarderCurrentSeat!!, currentRound.roundId),
            huData.points
        )
        currentRound.setTotalsPoints(getPlayersTotalPoints())
        return this
    }
    
    internal fun finishCurrentRoundByHuSelfpick(huData: HuData): Table {
        val currentRound = rounds.last()
        currentRound.finishRoundByHuSelfpick(
            getPlayerInitialSeatByCurrentSeat(huData.winnerCurrentSeat, currentRound.roundId),
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
        
        internal const val MAX_MCR_ROUNDS = 16
        internal const val MIN_MCR_POINTS = 8
        internal const val MAX_MCR_POINTS = 9999
        internal const val NUM_NO_WINNER_PLAYERS = 3
        internal const val NUM_DISCARD_NEUTRAL_PLAYERS = 2
        internal const val POINTS_DISCARD_NEUTRAL_PLAYERS = -8
        
        internal fun getHuSelfpickWinnerPoints(huPoints: Int) = (huPoints + 8) * 3
        
        internal fun getHuSelfpickDiscarderPoints(huPoints: Int) = -(huPoints + 8)
        
        internal fun getHuDiscardWinnerPoints(huPoints: Int) = huPoints + (8 * 3)
        
        internal fun getHuDiscardDiscarderPoints(huPoints: Int) = -(huPoints + 8)
        
        internal fun getPenaltyOtherPlayersPoints(penaltyPoints: Int) = penaltyPoints / NUM_NO_WINNER_PLAYERS
    }
}
