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
package com.etologic.mahjongscoring2.app.game.dialogs.ranking

import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.dtos.PlayerRanking
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.entities.UIGame

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

    fun generateRankingTable(uiGame: UIGame?): RankingData? {
        if (uiGame == null) return null
        val sortedPlayersRankings = getSortedPlayersRankings(uiGame)
        val bestHands = getBestHands(uiGame)
        return RankingData(
            sortedPlayersRankings,
            if (bestHands.isEmpty()) "-" else bestHands[0].handValue.toString(),
            if (bestHands.isEmpty()) "-" else bestHands[0].playerName,
            uiGame.rounds.size,
            uiGame.rounds.size.toString()
        )
    }

    private fun getSortedPlayersRankings(uiGame: UIGame): List<PlayerRanking> {
        var playersRankings = setPlayersNamesAndScores(uiGame)
        playersRankings = playersRankings.sortedByDescending { it.score }
        setPlayersTablePoints(playersRankings)
        return playersRankings
    }

    private fun setPlayersNamesAndScores(uiGame: UIGame): List<PlayerRanking> {
        val scores = uiGame.getPlayersTotalPointsWithPenalties()
        return listOf(
            PlayerRanking(uiGame.dbGame.nameP1, scores[0]),
            PlayerRanking(uiGame.dbGame.nameP2, scores[1]),
            PlayerRanking(uiGame.dbGame.nameP3, scores[2]),
            PlayerRanking(uiGame.dbGame.nameP4, scores[3])
        )
    }

    private fun setPlayersTablePoints(sortedPlayers: List<PlayerRanking>): List<PlayerRanking> {
        sortedPlayers[0].points = FIRST_POSITION_POINTS
        sortedPlayers[1].points = SECOND_POSITION_POINTS
        sortedPlayers[2].points = THIRD_POSITION_POINTS
        sortedPlayers[3].points = FOURTH_POSITION_POINTS
        val scorePlayerFirst = sortedPlayers[0].score
        val scorePlayerSecond = sortedPlayers[1].score
        val scorePlayerThird = sortedPlayers[2].score
        val scorePlayerFourth = sortedPlayers[3].score

        if (scorePlayerFirst == scorePlayerSecond &&
            scorePlayerSecond == scorePlayerThird &&
            scorePlayerThird == scorePlayerFourth
        ) {
            sortedPlayers[0].points = DRAW_4
            sortedPlayers[1].points = DRAW_4
            sortedPlayers[2].points = DRAW_4
            sortedPlayers[3].points = DRAW_4
            return sortedPlayers
        } else if (scorePlayerFirst == scorePlayerSecond && scorePlayerSecond == scorePlayerThird) {
            sortedPlayers[0].points = DRAW_FIRST_3
            sortedPlayers[1].points = DRAW_FIRST_3
            sortedPlayers[2].points = DRAW_FIRST_3
            return sortedPlayers
        } else if (scorePlayerSecond == scorePlayerThird && scorePlayerThird == scorePlayerFourth) {
            sortedPlayers[1].points = DRAW_LAST_3
            sortedPlayers[2].points = DRAW_LAST_3
            sortedPlayers[3].points = DRAW_LAST_3
            return sortedPlayers
        } else {
            if (scorePlayerFirst == scorePlayerSecond) {
                sortedPlayers[0].points = DRAW_FIRST_2
                sortedPlayers[1].points = DRAW_FIRST_2
            }
            if (scorePlayerSecond == scorePlayerThird) {
                sortedPlayers[1].points = DRAW_SECOND_2
                sortedPlayers[2].points = DRAW_SECOND_2
            }
            if (scorePlayerThird == scorePlayerFourth) {
                sortedPlayers[2].points = DRAW_LAST_2
                sortedPlayers[3].points = DRAW_LAST_2
            }
        }
        return sortedPlayers
    }

    private fun getBestHands(uiGame: UIGame): List<BestHand> {
        val bestHands = ArrayList<BestHand>()
        for (round in uiGame.rounds) {
            val roundHandPoints = round.handPoints
            val playerInitialPosition = uiGame.getPlayerInitialSeatByCurrentSeat(round.winnerInitialSeat!!)
            val playerName = uiGame.dbGame.getPlayerNameByInitialPosition(playerInitialPosition)
            val bestHand = BestHand(roundHandPoints, playerInitialPosition, playerName)
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
