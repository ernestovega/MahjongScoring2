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

import com.etologic.mahjongscoring2.app.utils.writeToFile
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.business.model.exceptions.GamesNotFoundException
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import javax.inject.Inject

class ExportAllGamesToCsvUseCase @Inject constructor(
    private val getAllGamesFlowUseCase: GetAllGamesFlowUseCase
) {
    @Throws(GameNotFoundException::class)
    suspend operator fun invoke(getExternalFilesDir: () -> File?): Result<List<File>> =
        runCatching {
            val uiGames = getAllGamesFlowUseCase.invoke().firstOrNull() ?: throw GamesNotFoundException()
            val csvText = buildCsvText(uiGames)
            val csvFile = writeToFile(
                name = "Games",
                csvText = csvText,
                externalFilesDir = getExternalFilesDir.invoke(),
            )
            listOf(csvFile)
        }

    private fun buildCsvText(uiGames: List<UiGame>): String =
        with(StringBuilder()) {
            buildHeader()
            buildRows(uiGames)
            toString()
        }

    private fun StringBuilder.buildHeader() {
        append("Game name,")
        append("Game id,")
        append("Round,")
        append("Winner,")
        append("Discarder,")
        append("Name P1,")
        append("Name P2,")
        append("Name P3,")
        append("Name P4,")
        append("Points P1,")
        append("Points P2,")
        append("Points P3,")
        append("Points P4,")
        append("Penalty P1,")
        append("Penalty P2,")
        append("Penalty P3,")
        append("Penalty P4")
        appendLine()
    }

    private fun StringBuilder.buildRows(uiGames: List<UiGame>) {
        uiGames.forEach { uiGame ->
            val nameP1 = normalizeName(uiGame.nameP1)
            val nameP2 = normalizeName(uiGame.nameP2)
            val nameP3 = normalizeName(uiGame.nameP3)
            val nameP4 = normalizeName(uiGame.nameP4)
            uiGame.uiRounds.forEach { uiRound ->
                /*GameName*/append("${uiGame.gameName},")
                /*GameId*/append("${uiGame.gameId},")
                /*Round*/append("${uiRound.roundNumber},")
                /*Winner*/append(
                when (uiRound.winnerInitialSeat) {
                    null,
                    NONE -> "-"

                    else -> normalizeName(uiGame.getPlayerNameByInitialPosition(uiRound.winnerInitialSeat))
                }
            ).also { append(",") }
                /*Discarder*/append(
                when (uiRound.discarderInitialSeat) {
                    null,
                    NONE -> "-"

                    else -> normalizeName(uiGame.getPlayerNameByInitialPosition(uiRound.discarderInitialSeat))
                }
            ).also { append(",") }
                /*Name P1*/append("${nameP1},")
                /*Name P2*/append("${nameP2},")
                /*Name P3*/append("${nameP3},")
                /*Name P4*/append("${nameP4},")
                /*Points P1*/append("${uiRound.pointsP1},")
                /*Points P2*/append("${uiRound.pointsP2},")
                /*Points P3*/append("${uiRound.pointsP3},")
                /*Points P4*/append("${uiRound.pointsP4},")
                /*Penalty P1*/append("${uiRound.penaltyP1},")
                /*Penalty P2*/append("${uiRound.penaltyP2},")
                /*Penalty P3*/append("${uiRound.penaltyP3},")
                /*Penalty P4*/append("${uiRound.penaltyP4}")
                appendLine()
            }
        }
    }
}