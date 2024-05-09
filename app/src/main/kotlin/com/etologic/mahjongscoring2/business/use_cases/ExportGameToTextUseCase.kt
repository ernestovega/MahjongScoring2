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
import com.etologic.mahjongscoring2.business.model.dtos.PlayerRanking
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.data_source.model.GameId
import kotlinx.coroutines.flow.firstOrNull
import java.lang.String.format
import java.util.Locale
import javax.inject.Inject

class ExportGameToTextUseCase @Inject constructor(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase
) {
    suspend operator fun invoke(
        gameId: GameId,
        getStringRes: (Int) -> String,
    ): Result<String> = runCatching {
        val uiGame = getOneGameFlowUseCase.invoke(gameId).firstOrNull() ?: throw GameNotFoundException(getStringRes(R.string.ups_something_wrong))
        buildText(uiGame, getStringRes)
    }

    private fun buildText(uiGame: UiGame, getStrRes: (Int) -> String): String =
        with(StringBuilder()) {
            buildFinalResultsText(uiGame, getStrRes)
            buildRoundsText(uiGame, getStrRes)
            toString()
        }

    private fun StringBuilder.buildFinalResultsText(uiGame: UiGame, getStrRes: (Int) -> String) {
        RankingTableHelper.generateRankingTable(uiGame)?.let { rankingData ->
            appendLine(uiGame.dbGame.gameName)
            appendLine()
            appendLine("${getStrRes(R.string.final_results).uppercase()}:")
            appendLine()
            appendLine("${getStrRes(R.string._1st)}:    ${rankingData.sortedPlayersRankings[0].toSignedString()}")
            appendLine("${getStrRes(R.string._2nd)}:    ${rankingData.sortedPlayersRankings[1].toSignedString()}")
            appendLine("${getStrRes(R.string._3rd)}:    ${rankingData.sortedPlayersRankings[2].toSignedString()}")
            appendLine("${getStrRes(R.string._4th)}:    ${rankingData.sortedPlayersRankings[3].toSignedString()}")
            if (uiGame.getBestHand().playerInitialPosition != NONE) {
                appendLine()
                appendLine("${getStrRes(R.string.best_hand).uppercase()}:")
                appendLine()
                appendLine("${uiGame.getBestHand().playerName} (${uiGame.getBestHand().handValue})")
            }
        }
            ?: appendLine(getStrRes(R.string.ups_something_wrong))
    }

    private fun StringBuilder.buildRoundsText(uiGame: UiGame, getStrRes: (Int) -> String) {
        appendLine()
        appendLine("${getStrRes(R.string.rounds).uppercase()} (${getStrRes(R.string.rounds_dont_count_penalties)}):")
        appendLine()
        uiGame.uiRounds.forEach { round ->
            if (round.dbRound.winnerInitialSeat != null || round.areTherePenalties()) {
                append(round.roundNumber.toString().padStart(2))
                appendLine()
                append("${getStrRes(R.string.winner_)} ")
                append(
                    when (round.dbRound.winnerInitialSeat) {
                        null -> "-"
                        NONE -> getStrRes(R.string.draw)
                        else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(round.dbRound.winnerInitialSeat!!))
                    }
                )
                appendLine()
                append("${getStrRes(R.string.points_)} ${round.dbRound.handPoints}")
                appendLine()
                append("${getStrRes(R.string.from)}: ")
                append(
                    when (round.dbRound.discarderInitialSeat) {
                        null -> "-"
                        NONE -> when (round.dbRound.winnerInitialSeat) {
                            null -> "-"
                            NONE -> getStrRes(R.string.draw)
                            else -> getStrRes(R.string.self_pick)
                        }

                        else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(round.dbRound.discarderInitialSeat!!))
                    }
                )
                appendLine()
                if (round.areTherePenalties()) {
                    appendLine()
                    appendLine("${getStrRes(R.string.penalties)}: ")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(EAST))}: ${round.dbRound.penaltyP1.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(SOUTH))}: ${round.dbRound.penaltyP2.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(WEST))}: ${round.dbRound.penaltyP3.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(NORTH))}: ${round.dbRound.penaltyP4.toSignedString()}")
                }
                if (round.dbRound.discarderInitialSeat != null && round.dbRound.discarderInitialSeat != NONE) {
                    appendLine()
                    appendLine("${getStrRes(R.string.partials)}: ")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(EAST))}: ${round.pointsP1.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(SOUTH))}: ${round.pointsP2.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(WEST))}: ${round.pointsP3.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(NORTH))}: ${round.pointsP4.toSignedString()}")
                    appendLine()
                    appendLine("${getStrRes(R.string.totals)}: ")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(EAST))}: ${round.totalPointsP1.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(SOUTH))}: ${round.totalPointsP2.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(WEST))}: ${round.totalPointsP3.toSignedString()}")
                    appendLine("${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(NORTH))}: ${round.totalPointsP4.toSignedString()}")
                    appendLine()
                    appendLine()
                }
            }
        }
    }
}

private fun PlayerRanking.toSignedString() = "$name    $points    (${format(Locale.getDefault(), "%+d", score)})"

private fun Int.toSignedString(): String = format(Locale.getDefault(), "%+d", this)
