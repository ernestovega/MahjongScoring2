/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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

package com.etologic.mahjongscoring2.app.screens.game.dialogs

import com.etologic.mahjongscoring2.business.model.dtos.PlayerRanking
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.entities.UiGame

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

    fun generateRankingTable(uiGame: UiGame?): RankingData? {
        if (uiGame == null) return null
        val sortedPlayersRankings = getSortedPlayersRankings(uiGame)
        val bestHand = uiGame.getBestHand()
        return RankingData(
            sortedPlayersRankings,
            if (bestHand.handValue == 0) "-" else bestHand.handValue.toString(),
            if (bestHand.handValue == 0) "-" else bestHand.playerName,
            uiGame.uiRounds.size,
            uiGame.uiRounds.size.toString()
        )
    }

    private fun getSortedPlayersRankings(uiGame: UiGame): List<PlayerRanking> {
        var playersRankings = setPlayersNamesAndScores(uiGame)
        playersRankings = playersRankings.sortedByDescending { it.score }
        setPlayersTablePoints(playersRankings)
        return playersRankings
    }

    private fun setPlayersNamesAndScores(uiGame: UiGame): List<PlayerRanking> = listOf(
        PlayerRanking(uiGame.nameP1, uiGame.ongoingRound.totalPointsP1),
        PlayerRanking(uiGame.nameP2, uiGame.ongoingRound.totalPointsP2),
        PlayerRanking(uiGame.nameP3, uiGame.ongoingRound.totalPointsP3),
        PlayerRanking(uiGame.nameP4, uiGame.ongoingRound.totalPointsP4)
    )

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
}
