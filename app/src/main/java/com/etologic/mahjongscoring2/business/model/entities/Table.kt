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
    
    private fun getPlayersTotalPointsByCurrentSeat(): IntArray {
        val points = getPlayersTotalPoints()
        val pointsByCurrentSeat = intArrayOf(0, 0, 0, 0)
        val roundId = rounds.last().roundId
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(roundId).code] = points[EAST.code]
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(roundId).code] = points[SOUTH.code]
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(roundId).code] = points[WEST.code]
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(roundId).code] = points[NORTH.code]
        return pointsByCurrentSeat
    }
    
    internal fun getPlayersTotalPointsStringByCurrentSeat(): List<String> =
        getPlayersTotalPointsByCurrentSeat().map { String.format(Locale.getDefault(), "%d", it) }
    
    internal fun getPlayersPenaltiesByCurrentSeat(): IntArray {
        val pointsByCurrentSeat = intArrayOf(0, 0, 0, 0)
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
    
    internal fun resetTotals(): Table {
        rounds.forEachIndexed { index, round ->
            if(round.isEnded) {
                val newRoundTotals = getTotalPointsUntilRound(index)
                round.totalPointsP1 = newRoundTotals[0]
                round.totalPointsP2 = newRoundTotals[1]
                round.totalPointsP3 = newRoundTotals[2]
                round.totalPointsP4 = newRoundTotals[3]
            }
        }
        return this
    }
    
    private fun getTotalPointsUntilRound(index: Int): IntArray {
        val totalPoints = intArrayOf(0,0,0,0)
        for (i in 0..index) {
            totalPoints[0]+= rounds[i].pointsP1
            totalPoints[1]+= rounds[i].pointsP2
            totalPoints[2]+= rounds[i].pointsP3
            totalPoints[3]+= rounds[i].pointsP4
        }
        return totalPoints
    }
    
    internal companion object {
        
        internal const val NUM_MCR_PLAYERS = 4
        internal const val MAX_MCR_ROUNDS = 16
        internal const val MIN_MCR_POINTS = 8
        internal const val MAX_MCR_POINTS = 9999
        internal const val NUM_NO_WINNER_PLAYERS = 3
        internal const val POINTS_DISCARD_NEUTRAL_PLAYERS = -8
        
        internal fun getHuSelfpickWinnerPoints(huPoints: Int) = (huPoints + MIN_MCR_POINTS) * NUM_NO_WINNER_PLAYERS
        
        internal fun getHuSelfpickDiscarderPoints(huPoints: Int) = -(huPoints + MIN_MCR_POINTS)
        
        internal fun getHuDiscardWinnerPoints(huPoints: Int) = huPoints + (MIN_MCR_POINTS * NUM_NO_WINNER_PLAYERS)
        
        internal fun getHuDiscardDiscarderPoints(huPoints: Int) = -(huPoints + MIN_MCR_POINTS)
        
        internal fun getPenaltyOtherPlayersPoints(penaltyPoints: Int) = penaltyPoints / NUM_NO_WINNER_PLAYERS
    }
}
