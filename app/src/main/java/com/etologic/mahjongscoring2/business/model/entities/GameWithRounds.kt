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
        namesListByCurrentSeat[getEastPlayerCurrentSeat(roundId).code] = game.nameP1
        namesListByCurrentSeat[getSouthSeatPlayerByRound(roundId).code] = game.nameP2
        namesListByCurrentSeat[getWestSeatPlayerByRound(roundId).code] = game.nameP3
        namesListByCurrentSeat[getNorthSeatPlayerByRound(roundId).code] = game.nameP4
        return namesListByCurrentSeat
    }
    
    internal fun getPlayersTotalPointsString(): Array<String> {
        val points = getPlayersTotalPoints()
        return arrayOf(points[EAST.code].toString(), points[SOUTH.code].toString(), points[WEST.code].toString(), points[NORTH.code].toString())
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
    
    internal fun getRoundsWithBestHand(): List<Round> {
        val bestHand = getBestHand()
        for (round in rounds) {
            val isBestHandRound = round.handPoints >= bestHand.handValue && round.winnerInitialPosition == bestHand.playerInitialPosition
            round.isBestHand = isBestHandRound
        }
        return rounds
    }
    
    internal fun getBestHand(): BestHand {
        val bestHand = BestHand()
        for (round in rounds) {
            if (round.handPoints > bestHand.handValue) {
                bestHand.handValue = round.handPoints
                bestHand.playerName = game.getPlayerNameByInitialPosition(round.winnerInitialPosition)
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
    
    internal fun getEastPlayerCurrentSeat(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> EAST
            5, 6, 7, 8 -> SOUTH
            9, 10, 11, 12 -> WEST
            13, 14, 15, 16 -> NORTH
            else -> EAST
        }
    }
    
    internal fun getSouthSeatPlayerByRound(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> SOUTH
            5, 6, 7, 8 -> EAST
            9, 10, 11, 12 -> NORTH
            13, 14, 15, 16 -> WEST
            else -> SOUTH
        }
    }
    
    internal fun getWestSeatPlayerByRound(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> WEST
            5, 6, 7, 8 -> NORTH
            9, 10, 11, 12 -> SOUTH
            13, 14, 15, 16 -> EAST
            else -> WEST
        }
    }
    
    internal fun getNorthSeatPlayerByRound(roundId: Int): TableWinds {
        return when (roundId) {
            1, 2, 3, 4 -> NORTH
            5, 6, 7, 8 -> WEST
            9, 10, 11, 12 -> EAST
            13, 14, 15, 16 -> SOUTH
            else -> NORTH
        }
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
}
