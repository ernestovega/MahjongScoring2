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
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.GameId
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import javax.inject.Inject

class ExportGameToCsvUseCase @Inject constructor(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase
) {
    @Throws(GameNotFoundException::class)
    suspend operator fun invoke(gameId: GameId, getExternalFilesDir: () -> File?): Result<List<File>> =
        runCatching {
            val uiGame = getOneGameFlowUseCase.invoke(gameId).firstOrNull() ?: throw GameNotFoundException()
            val csvText = buildCsvText(uiGame)
            val csvFile = writeToFile(
                name = "Game_${normalizeName(uiGame.gameName).replace(" ", "-")}",
                csvText = csvText,
                externalFilesDir = getExternalFilesDir.invoke(),
            )
            listOf(csvFile)
        }

    private fun buildCsvText(uiGame: UiGame): String =
        with(StringBuilder()) {
            buildHeader(uiGame)
            buildRows(uiGame)
            toString()
        }

    private fun StringBuilder.buildHeader(uiGame: UiGame) {
        val initialEastPlayerName = normalizeName(uiGame.getPlayerNameByInitialPosition(EAST))
        val initialSouthPlayerName = normalizeName(uiGame.getPlayerNameByInitialPosition(SOUTH))
        val initialWestPlayerName = normalizeName(uiGame.getPlayerNameByInitialPosition(WEST))
        val initialNorthPlayerName = normalizeName(uiGame.getPlayerNameByInitialPosition(NORTH))

        append("Round,")
        append("Winner,")
        append("Discarder,")
        append("Points_$initialEastPlayerName,")
        append("Points_$initialSouthPlayerName,")
        append("Points_$initialWestPlayerName,")
        append("Points_$initialNorthPlayerName,")
        append("Penalty_$initialEastPlayerName,")
        append("Penalty_$initialSouthPlayerName,")
        append("Penalty_$initialWestPlayerName,")
        append("Penalty_$initialNorthPlayerName")
        appendLine()
    }

    private fun StringBuilder.buildRows(uiGame: UiGame) {
        uiGame.uiRounds.forEach { uiRound ->
            append("${uiRound.roundNumber},")
            append(
                when (uiRound.dbRound.winnerInitialSeat) {
                    null,
                    NONE -> "-"

                    else -> normalizeName(uiGame.getPlayerNameByInitialPosition(uiRound.dbRound.winnerInitialSeat!!))
                }
            ).also { append(",") }
            append(
                when (uiRound.dbRound.discarderInitialSeat) {
                    null,
                    NONE -> "-"

                    else -> normalizeName(uiGame.getPlayerNameByInitialPosition(uiRound.dbRound.discarderInitialSeat!!))
                }
            ).also { append(",") }
            append("${uiRound.pointsP1},")
            append("${uiRound.pointsP2},")
            append("${uiRound.pointsP3},")
            append("${uiRound.pointsP4},")
            append("${uiRound.dbRound.penaltyP1},")
            append("${uiRound.dbRound.penaltyP2},")
            append("${uiRound.dbRound.penaltyP3},")
            append(uiRound.dbRound.penaltyP4)
            appendLine()
        }
    }
}