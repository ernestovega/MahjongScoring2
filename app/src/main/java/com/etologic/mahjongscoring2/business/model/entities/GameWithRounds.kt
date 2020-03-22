package com.etologic.mahjongscoring2.business.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import java.util.*

class GameWithRounds(@field:Embedded var game: Game) {
    
    @Relation(parentColumn = "gameId", entityColumn = "gameId")
    var rounds: List<Round> = ArrayList()
    
    fun getPlayersTotalPointsString(): Array<String> {
        val points = getPlayersTotalPoints()
        return arrayOf(points[EAST.code].toString(), points[SOUTH.code].toString(), points[WEST.code].toString(), points[NORTH.code].toString())
    }
    
    fun getPlayersTotalPoints(): IntArray {
        val points = intArrayOf(0, 0, 0, 0)
        for (round in rounds) {
            points[EAST.code] += round.pointsP1
            points[SOUTH.code] += round.pointsP2
            points[WEST.code] += round.pointsP3
            points[NORTH.code] += round.pointsP4
        }
        return points
    }
    
    fun getRoundsWithBestHand(): List<Round> {
        val bestHand = getBestHand()
        for (round in rounds) {
            val isBestHandRound = round.handPoints == bestHand.handValue && round.winnerInitialPosition === bestHand.playerInitialPosition
            round.isBestHand = isBestHandRound
        }
        return rounds
    }
    
    fun getBestHand(): BestHand {
        val bestHand = BestHand()
        for (round in rounds) {
            if (round.handPoints > bestHand.handValue) {
                bestHand.handValue = round.handPoints
                bestHand.playerName = game.getPlayerNameByInitialPosition(round.winnerInitialPosition)
            }
        }
        return bestHand
    }
    
    fun getDuration(): Long {
        var gameDuration: Long = 0
        for (round in rounds) gameDuration += round.roundDuration
        return gameDuration
    }
    
    fun getCopy(): GameWithRounds {
        val gameWithRounds = GameWithRounds(game.copy)
        val newRounds = ArrayList<Round>(rounds.size)
        rounds.map { newRounds.add(it.getCopy()) }
        gameWithRounds.rounds = newRounds
        return gameWithRounds
    }
}
