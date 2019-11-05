package es.etologic.mahjongscoring2.app.game.game_table

import es.etologic.mahjongscoring2.app.utils.DateTimeUtils
import es.etologic.mahjongscoring2.domain.model.*
import java.util.*
import java.util.Collections.reverse
import java.util.Collections.sort

object RankingTableHelper {
    
    private const val FIRST_POSITION_POINTS = "4"
    private const val SECOND_POSITION_POINTS = "2"
    private const val THIRD_POSITION_POINTS = "1"
    private const val FOURTH_POSITION_POINTS = "0"
    private const val DRAW_4 = "1.75"
    private const val DRAW_FIRST_3 = "2.33"
    private const val DRAW_LAST_3 = "1"
    private const val DRAW_FIRST_2 = "3"
    private const val DRAW_SECOND_2 = "1.5"
    private const val DRAW_LAST_2 = "0.5"
    
    fun generateRankingTable(gameWithRounds: GameWithRounds?): RankingTable? {
        if (gameWithRounds == null) return null
        val sortedPlayersRankings = getSortedPlayersRankings(gameWithRounds.game, gameWithRounds.rounds)
        val bestHands = getBestHands(gameWithRounds.rounds)
        return RankingTable(
            sortedPlayersRankings,
            if (bestHands.isEmpty()) "-" else bestHands[0].handValue.toString(),
            gameWithRounds.rounds.size,
            gameWithRounds.rounds.size.toString(),
            DateTimeUtils.getPrettyDuration(gameWithRounds.rounds)
        )
    }
    
    private fun getSortedPlayersRankings(game: Game, rounds: List<Round>): List<PlayerRanking> {
        val playersRankings = setPlayersNamesAndScores(game, rounds)
        sort(playersRankings)
        reverse(playersRankings)
        setPlayersTablePoints(playersRankings)
        return playersRankings
    }
    
    private fun setPlayersNamesAndScores(game: Game, rounds: List<Round>): List<PlayerRanking> {
        val playersRankings = ArrayList<PlayerRanking>(4)
        val totalScoreP1 = GameRounds.getTotalScoreP1(rounds)
        val totalScoreP2 = GameRounds.getTotalScoreP2(rounds)
        val totalScoreP3 = GameRounds.getTotalScoreP3(rounds)
        val totalScoreP4 = GameRounds.getTotalScoreP4(rounds)
        playersRankings.add(PlayerRanking(game.nameP1, totalScoreP1.toString()))
        playersRankings.add(PlayerRanking(game.nameP2, totalScoreP2.toString()))
        playersRankings.add(PlayerRanking(game.nameP3, totalScoreP3.toString()))
        playersRankings.add(PlayerRanking(game.nameP4, totalScoreP4.toString()))
        return playersRankings
    }
    
    private fun setPlayersTablePoints(sorteredByScorePlayersRankings: List<PlayerRanking>): List<PlayerRanking> {
        sorteredByScorePlayersRankings[0].points = FIRST_POSITION_POINTS
        sorteredByScorePlayersRankings[1].points = SECOND_POSITION_POINTS
        sorteredByScorePlayersRankings[2].points = THIRD_POSITION_POINTS
        sorteredByScorePlayersRankings[3].points = FOURTH_POSITION_POINTS
        val scorePlayerFirst = sorteredByScorePlayersRankings[0].score
        val scorePlayerSecond = sorteredByScorePlayersRankings[1].score
        val scorePlayerThird = sorteredByScorePlayersRankings[2].score
        val scorePlayerFourth = sorteredByScorePlayersRankings[3].score
        
        if (scorePlayerFirst == scorePlayerSecond &&
            scorePlayerSecond == scorePlayerThird &&
            scorePlayerThird == scorePlayerFourth
        ) {
            sorteredByScorePlayersRankings[0].points = DRAW_4
            sorteredByScorePlayersRankings[1].points = DRAW_4
            sorteredByScorePlayersRankings[2].points = DRAW_4
            sorteredByScorePlayersRankings[3].points = DRAW_4
            return sorteredByScorePlayersRankings
        } else if (scorePlayerFirst == scorePlayerSecond && scorePlayerSecond == scorePlayerThird) {
            sorteredByScorePlayersRankings[0].points = DRAW_FIRST_3
            sorteredByScorePlayersRankings[1].points = DRAW_FIRST_3
            sorteredByScorePlayersRankings[2].points = DRAW_FIRST_3
            return sorteredByScorePlayersRankings
        } else if (scorePlayerSecond == scorePlayerThird && scorePlayerThird == scorePlayerFourth) {
            sorteredByScorePlayersRankings[1].points = DRAW_LAST_3
            sorteredByScorePlayersRankings[2].points = DRAW_LAST_3
            sorteredByScorePlayersRankings[3].points = DRAW_LAST_3
            return sorteredByScorePlayersRankings
        } else {
            if (scorePlayerFirst == scorePlayerSecond) {
                sorteredByScorePlayersRankings[0].points = DRAW_FIRST_2
                sorteredByScorePlayersRankings[1].points = DRAW_FIRST_2
            }
            if (scorePlayerSecond == scorePlayerThird) {
                sorteredByScorePlayersRankings[1].points = DRAW_SECOND_2
                sorteredByScorePlayersRankings[2].points = DRAW_SECOND_2
            }
            if (scorePlayerThird == scorePlayerFourth) {
                sorteredByScorePlayersRankings[2].points = DRAW_LAST_2
                sorteredByScorePlayersRankings[3].points = DRAW_LAST_2
            }
        }
        return sorteredByScorePlayersRankings
    }
    
    private fun getBestHands(rounds: List<Round>): List<BestHand> {
        val bestHands = ArrayList<BestHand>()
        for (round in rounds) {
            val roundHandPoints = round.handPoints
            val bestHand = BestHand()
            bestHand.handValue = roundHandPoints
            bestHand.playerInitialPosition = round.winnerInitialPosition
            if (bestHands.isEmpty() || roundHandPoints == bestHands[0].handValue) {
                bestHands.add(bestHand)
            } else if (roundHandPoints > bestHands[0].handValue) {
                bestHands.clear()
                bestHands.add(bestHand)
            }
        }
        return bestHands
    }
}
