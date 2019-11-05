package es.etologic.mahjongscoring2.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
import java.util.*

class GameWithRounds(@field:Embedded var game: Game) {
    
    @Relation(parentColumn = "gameId", entityColumn = "gameId")
    var rounds: List<Round> = ArrayList()
    
    fun getPlayersTotalPointsString(): Array<String> {
        val points = getPlayersTotalPoints()
        return arrayOf(points[EAST.index].toString(), points[SOUTH.index].toString(), points[WEST.index].toString(), points[NORTH.index].toString())
    }
    
    fun getPlayersTotalPoints(): IntArray {
        val points = intArrayOf(0, 0, 0, 0)
        for (round in rounds) {
            points[EAST.index] += round.pointsP1
            points[SOUTH.index] += round.pointsP2
            points[WEST.index] += round.pointsP3
            points[NORTH.index] += round.pointsP4
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
        for (round in rounds) newRounds.add(round.getCopy())
        gameWithRounds.rounds = newRounds
        return gameWithRounds
    }
}
