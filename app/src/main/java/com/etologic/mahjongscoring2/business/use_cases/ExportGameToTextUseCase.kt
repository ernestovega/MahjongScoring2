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

package com.etologic.mahjongscoring2.business.use_cases

import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingTableHelper
import com.etologic.mahjongscoring2.business.model.dtos.toText
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.data_source.model.GameId
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ExportGameToTextUseCase @Inject constructor(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase
) {
    suspend operator fun invoke(
        gameId: GameId,
        getStringRes: (Int) -> String,
    ): String {
        val uiGame = getOneGameFlowUseCase.invoke(gameId).firstOrNull() ?: return getStringRes(R.string.ups_something_wrong)
        return buildText(uiGame, getStringRes)
    }

    private fun buildText(uiGame: UIGame, getStrRes: (Int) -> String): String =
        with(StringBuilder()) {
            buildResultsText(uiGame, getStrRes)
            buildRoundsText(uiGame, getStrRes)
            toString()
        }

    private fun StringBuilder.buildResultsText(uiGame: UIGame, getStrRes: (Int) -> String) {
        RankingTableHelper.generateRankingTable(uiGame)?.let { rankingData ->
            appendLine(uiGame.dbGame.gameName)
            appendLine()
            appendLine("${getStrRes(R.string.final_results).uppercase()}:")
            appendLine()
            appendLine("${getStrRes(R.string._1st)}:    ${rankingData.sortedPlayersRankings[0].toText()}")
            appendLine("${getStrRes(R.string._2nd)}:    ${rankingData.sortedPlayersRankings[1].toText()}")
            appendLine("${getStrRes(R.string._3rd)}:    ${rankingData.sortedPlayersRankings[2].toText()}")
            appendLine("${getStrRes(R.string._4th)}:    ${rankingData.sortedPlayersRankings[3].toText()}")
            appendLine()
            appendLine("${getStrRes(R.string.best_hand).uppercase()}:")
            appendLine()
            appendLine("${uiGame.getBestHand().playerName} (${uiGame.getBestHand().handValue})")
        }
            ?: appendLine(getStrRes(R.string.ups_something_wrong))
    }

    private fun StringBuilder.buildRoundsText(uiGame: UIGame, getStrRes: (Int) -> String) {
        appendLine()
        appendLine("${getStrRes(R.string.rounds).uppercase()}:")
        appendLine()
        uiGame.rounds.forEach { round ->
            append(round.roundNumber.toString().padStart(2, ' '))
            append("  -  ")
            append(uiGame.dbGame.getPlayerNameByInitialPosition(round.winnerInitialSeat!!))
            append("  -  ")
            append(round.handPoints.toString().padStart(2, ' '))
            append("  ")
            append(getStrRes(R.string.points_).dropLast(1).lowercase())
            append("  -  ")
            if (round.discarderInitialSeat == NONE) {
                append(getStrRes(R.string.self_pick))
            } else {
                append("${getStrRes(R.string.from)} ${uiGame.dbGame.getPlayerNameByInitialPosition(round.discarderInitialSeat!!)}")
            }
            append(".")
            appendLine()
        }
    }
}
