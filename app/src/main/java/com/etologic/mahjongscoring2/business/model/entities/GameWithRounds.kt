package com.etologic.mahjongscoring2.business.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import java.util.*

class GameWithRounds(@field:Embedded var game: Game) {
    
    @Relation(parentColumn = "gameId", entityColumn = "gameId")
    var rounds: List<Round> = ArrayList()
    
    internal fun getCopy(): GameWithRounds {
        val gameWithRounds = GameWithRounds(game.copy)
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
    
    internal fun getPlayersTotalPointsStringByCurrentSeat(): Array<String> {
        val points = getPlayersTotalPoints()
        val pointsByCurrentSeat: Array<String> = arrayOf("", "", "", "")
        val roundId = rounds.last().roundId
        pointsByCurrentSeat[getInitialEastPlayerCurrentSeat(roundId).code] = points[EAST.code].toString()
        pointsByCurrentSeat[getInitialSouthPlayerCurrentSeat(roundId).code] = points[SOUTH.code].toString()
        pointsByCurrentSeat[getInitialWestPlayerCurrentSeat(roundId).code] = points[WEST.code].toString()
        pointsByCurrentSeat[getInitialNorthPlayerCurrentSeat(roundId).code] = points[NORTH.code].toString()
        return pointsByCurrentSeat
    }
    
    private fun getInitialEastPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> EAST
            5, 6, 7, 8 -> SOUTH
            9, 10, 11, 12 -> WEST
            13, 14, 15, 16 -> NORTH
            else -> EAST
        }
    }
    
    private fun getInitialSouthPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> SOUTH
            5, 6, 7, 8 -> EAST
            9, 10, 11, 12 -> NORTH
            13, 14, 15, 16 -> WEST
            else -> SOUTH
        }
    }
    
    private fun getInitialWestPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> WEST
            5, 6, 7, 8 -> NORTH
            9, 10, 11, 12 -> SOUTH
            13, 14, 15, 16 -> EAST
            else -> WEST
        }
    }
    
    private fun getInitialNorthPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> NORTH
            5, 6, 7, 8 -> WEST
            9, 10, 11, 12 -> EAST
            13, 14, 15, 16 -> SOUTH
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
        return rounds.dropLastWhile { !it.isEnded }
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
    
    internal companion object {
    
        internal const val MAX_MCR_ROUNDS = 16
        internal const val MIN_MCR_POINTS = 8
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
