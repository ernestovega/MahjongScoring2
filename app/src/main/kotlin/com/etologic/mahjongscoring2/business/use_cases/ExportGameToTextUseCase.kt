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

import com.etologic.mahjongscoring2.app.screens.game.dialogs.RankingTableHelper
import com.etologic.mahjongscoring2.app.utils.DateTimeUtils.prettifyOneLine
import com.etologic.mahjongscoring2.business.model.dtos.PlayerRanking
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.business.model.exceptions.RankingDataGenerationException
import kotlinx.coroutines.flow.firstOrNull
import java.lang.String.format
import java.util.Locale
import javax.inject.Inject

class ExportGameToTextUseCase @Inject constructor(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
    suspend operator fun invoke(gameId: GameId): Result<String> =
        runCatching {
            getOneGameFlowUseCase.invoke(gameId)
                .firstOrNull()
                ?.let { uiGame ->
                    val text = buildText(uiGame)
                    text
                }
                ?: throw GameNotFoundException(gameId)
        }

    private fun buildText(uiGame: UiGame): String =
        with(StringBuilder()) {
            buildFinalResultsText(uiGame)
//            buildRoundsText(uiGame, getStrRes)
            toString()
        }

    private fun StringBuilder.buildFinalResultsText(uiGame: UiGame) {
        RankingTableHelper.generateRankingTable(uiGame)?.let { rankingData ->
//            appendLine("- GAME:")
//            appendLine()
            appendLine(uiGame.gameName.ifBlank { uiGame.startDate.prettifyOneLine() })
            appendLine()
//            appendLine("- FINAL RESULTS:")
//            appendLine()
            appendLine("1st:    ${rankingData.sortedPlayersRankings[0].toSignedString()}")
            appendLine("2nd:    ${rankingData.sortedPlayersRankings[1].toSignedString()}")
            appendLine("3rd:    ${rankingData.sortedPlayersRankings[2].toSignedString()}")
            appendLine("4th:    ${rankingData.sortedPlayersRankings[3].toSignedString()}")
            appendLine()
            if (uiGame.getBestHand().playerInitialPosition != NONE) {
                append("Best hand:    ${uiGame.getBestHand().handValue}  -  ${uiGame.getBestHand().playerName}")
                appendLine()
            }
        }
            ?: throw RankingDataGenerationException()
    }

//    private fun StringBuilder.buildRoundsText(uiGame: UiGame, getStrRes: (Int) -> String) {
//        val initialEastPlayerName = normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(EAST))
//        val initialSouthPlayerName = normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(SOUTH))
//        val initialWestPlayerName = normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(WEST))
//        val initialNorthPlayerName = normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(NORTH))
//
//        appendLine()
//        appendLine("- ${getStrRes(R.string.rounds).uppercase()}:")
//        appendLine("(${getStrRes(R.string.rounds_dont_count_penalties)})")
//        appendLine()
//        uiGame.uiRounds.forEach { round ->
//            if (round.winnerInitialSeat != null || round.areTherePenalties()) {
//                append(round.roundNumber.toString().padStart(2))
//                appendLine()
//                append("${getStrRes(R.string.winner_)} ")
//                append(
//                    when (round.winnerInitialSeat) {
//                        null -> "-"
//                        NONE -> getStrRes(R.string.draw)
//                        else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(round.winnerInitialSeat!!))
//                    }
//                )
//                appendLine()
//                append("${getStrRes(R.string.points_)} ${round.dbRound.handPoints}")
//                appendLine()
//                append("${getStrRes(R.string.from)}: ")
//                append(
//                    when (round.discarderInitialSeat) {
//                        null -> "-"
//                        NONE -> when (round.dbRound.winnerInitialSeat) {
//                            null -> "-"
//                            NONE -> getStrRes(R.string.draw)
//                            else -> getStrRes(R.string.self_pick)
//                        }
//
//                        else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(round.dbRound.discarderInitialSeat!!))
//                    }
//                )
//                appendLine()
//                if (round.areTherePenalties()) {
//                    appendLine()
//                    appendLine("${getStrRes(R.string.penalties)}: ")
//                    appendLine("$initialEastPlayerName: ${round.penaltyP1.toSignedString()}")
//                    appendLine("$initialSouthPlayerName: ${round.penaltyP2.toSignedString()}")
//                    appendLine("$initialWestPlayerName: ${round.penaltyP3.toSignedString()}")
//                    appendLine("$initialNorthPlayerName: ${round.penaltyP4.toSignedString()}")
//                }
//                if (round.discarderInitialSeat != null && round.discarderInitialSeat != NONE) {
//                    appendLine()
//                    appendLine("${getStrRes(R.string.partials)}: ")
//                    appendLine("$initialEastPlayerName: ${round.pointsP1.toSignedString()}")
//                    appendLine("$initialSouthPlayerName: ${round.pointsP2.toSignedString()}")
//                    appendLine("$initialWestPlayerName: ${round.pointsP3.toSignedString()}")
//                    appendLine("$initialNorthPlayerName: ${round.pointsP4.toSignedString()}")
//                    appendLine()
//                    appendLine("${getStrRes(R.string.totals)}: ")
//                    appendLine("$initialEastPlayerName: ${round.totalPointsP1.toSignedString()}")
//                    appendLine("$initialSouthPlayerName: ${round.totalPointsP2.toSignedString()}")
//                    appendLine("$initialWestPlayerName: ${round.totalPointsP3.toSignedString()}")
//                    appendLine("$initialNorthPlayerName: ${round.totalPointsP4.toSignedString()}")
//                    appendLine()
//                }
//                appendLine()
//            }
//        }
//    }
}

private fun PlayerRanking.toSignedString() = "$name    $points    (${format(Locale.getDefault(), "%+d", score)})"
