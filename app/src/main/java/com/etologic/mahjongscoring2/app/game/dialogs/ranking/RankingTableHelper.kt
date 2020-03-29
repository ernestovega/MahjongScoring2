package com.etologic.mahjongscoring2.app.game.dialogs.ranking

import com.etologic.mahjongscoring2.app.utils.DateTimeUtils
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.dtos.PlayerRanking
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.entities.Round
import java.util.*
import java.util.Collections.reverse

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
    
    fun generateRankingTable(table: Table?): RankingData? {
        if (table == null) return null
        val sortedPlayersRankings =
            getSortedPlayersRankings(
                table,
                table.rounds
            )
        val bestHands =
            getBestHands(table)
        return RankingData(
            sortedPlayersRankings,
            if (bestHands.isEmpty()) "-" else bestHands[0].handValue.toString(),
            if (bestHands.isEmpty()) "-" else bestHands[0].playerName,
            table.rounds.size,
            table.rounds.size.toString(),
            DateTimeUtils.getPrettyDuration(table.rounds)
        )
    }
    
    private fun getSortedPlayersRankings(table: Table, rounds: List<Round>): List<PlayerRanking> {
        val playersRankings = setPlayersNamesAndScores(
            table,
            rounds
        )
        playersRankings.sortedWith(compareBy(PlayerRanking::points, PlayerRanking::score))
        reverse(playersRankings)
        setPlayersTablePoints(playersRankings)
        return playersRankings
    }
    
    private fun setPlayersNamesAndScores(table: Table, rounds: List<Round>): List<PlayerRanking> {
        val playersRankings = ArrayList<PlayerRanking>(4)
        val totalScoreP1 = table.getTotalScoreP1(rounds)
        val totalScoreP2 = table.getTotalScoreP2(rounds)
        val totalScoreP3 = table.getTotalScoreP3(rounds)
        val totalScoreP4 = table.getTotalScoreP4(rounds)
        playersRankings.add(PlayerRanking(table.game.nameP1, totalScoreP1.toString()))
        playersRankings.add(PlayerRanking(table.game.nameP2, totalScoreP2.toString()))
        playersRankings.add(PlayerRanking(table.game.nameP3, totalScoreP3.toString()))
        playersRankings.add(PlayerRanking(table.game.nameP4, totalScoreP4.toString()))
        return playersRankings
    }
    
    private fun setPlayersTablePoints(sorteredByScorePlayersRankings: List<PlayerRanking>): List<PlayerRanking> {
        sorteredByScorePlayersRankings[0].points =
            FIRST_POSITION_POINTS
        sorteredByScorePlayersRankings[1].points =
            SECOND_POSITION_POINTS
        sorteredByScorePlayersRankings[2].points =
            THIRD_POSITION_POINTS
        sorteredByScorePlayersRankings[3].points =
            FOURTH_POSITION_POINTS
        val scorePlayerFirst = sorteredByScorePlayersRankings[0].score
        val scorePlayerSecond = sorteredByScorePlayersRankings[1].score
        val scorePlayerThird = sorteredByScorePlayersRankings[2].score
        val scorePlayerFourth = sorteredByScorePlayersRankings[3].score
        
        if (scorePlayerFirst == scorePlayerSecond &&
            scorePlayerSecond == scorePlayerThird &&
            scorePlayerThird == scorePlayerFourth
        ) {
            sorteredByScorePlayersRankings[0].points =
                DRAW_4
            sorteredByScorePlayersRankings[1].points =
                DRAW_4
            sorteredByScorePlayersRankings[2].points =
                DRAW_4
            sorteredByScorePlayersRankings[3].points =
                DRAW_4
            return sorteredByScorePlayersRankings
        } else if (scorePlayerFirst == scorePlayerSecond && scorePlayerSecond == scorePlayerThird) {
            sorteredByScorePlayersRankings[0].points =
                DRAW_FIRST_3
            sorteredByScorePlayersRankings[1].points =
                DRAW_FIRST_3
            sorteredByScorePlayersRankings[2].points =
                DRAW_FIRST_3
            return sorteredByScorePlayersRankings
        } else if (scorePlayerSecond == scorePlayerThird && scorePlayerThird == scorePlayerFourth) {
            sorteredByScorePlayersRankings[1].points =
                DRAW_LAST_3
            sorteredByScorePlayersRankings[2].points =
                DRAW_LAST_3
            sorteredByScorePlayersRankings[3].points =
                DRAW_LAST_3
            return sorteredByScorePlayersRankings
        } else {
            if (scorePlayerFirst == scorePlayerSecond) {
                sorteredByScorePlayersRankings[0].points =
                    DRAW_FIRST_2
                sorteredByScorePlayersRankings[1].points =
                    DRAW_FIRST_2
            }
            if (scorePlayerSecond == scorePlayerThird) {
                sorteredByScorePlayersRankings[1].points =
                    DRAW_SECOND_2
                sorteredByScorePlayersRankings[2].points =
                    DRAW_SECOND_2
            }
            if (scorePlayerThird == scorePlayerFourth) {
                sorteredByScorePlayersRankings[2].points =
                    DRAW_LAST_2
                sorteredByScorePlayersRankings[3].points =
                    DRAW_LAST_2
            }
        }
        return sorteredByScorePlayersRankings
    }
    
    private fun getBestHands(table: Table): List<BestHand> {
        val bestHands = ArrayList<BestHand>()
        for (round in table.rounds) {
            val roundHandPoints = round.handPoints
            val bestHand = BestHand()
            bestHand.handValue = roundHandPoints
            bestHand.playerInitialPosition = round.winnerInitialSeat
            bestHand.playerName = table.game.getPlayerNameByInitialPosition(bestHand.playerInitialPosition)
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
