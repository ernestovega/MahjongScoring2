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

import androidx.annotation.VisibleForTesting
import com.etologic.mahjongscoring2.app.utils.writeToFile
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.business.use_cases.utils.normalizeName
import java.io.File
import javax.inject.Inject

class ExportGameToCsvUseCase @Inject constructor(
    private val getOneGameUseCase: GetOneGameUseCase,
) {
    suspend operator fun invoke(gameId: GameId, getExternalFilesDir: () -> File?): Result<List<File>> =
        getOneGameUseCase(gameId)
            .mapCatching { uiGame ->
                val csvText = buildCsvText(uiGame)
                val csvFile = writeToFile(
                    name = normalizeName(uiGame.gameName).replace(" ", "_"),
                    csvText = csvText,
                    externalFilesDir = getExternalFilesDir.invoke(),
                )
                listOf(csvFile)
            }

    @VisibleForTesting
    fun buildCsvText(uiGame: UiGame): String =
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
        append("Hand Points,")
        append("Points $initialEastPlayerName,")
        append("Points $initialSouthPlayerName,")
        append("Points $initialWestPlayerName,")
        append("Points $initialNorthPlayerName,")
        append("Penalty $initialEastPlayerName,")
        append("Penalty $initialSouthPlayerName,")
        append("Penalty $initialWestPlayerName,")
        append("Penalty $initialNorthPlayerName,")
        append("Game name,")
        append("Game start date,")
        append("Game end date")
        appendLine()
    }

    private fun StringBuilder.buildRows(uiGame: UiGame) {
        uiGame.uiRounds.forEach { uiRound ->
            append("${uiRound.roundNumber},")
            append(
                when (uiRound.winnerInitialSeat) {
                    null,
                    NONE -> "-"

                    else -> normalizeName(uiGame.getPlayerNameByInitialPosition(uiRound.winnerInitialSeat))
                }
            ).also { append(",") }
            append(
                when (uiRound.discarderInitialSeat) {
                    null,
                    NONE -> "-"

                    else -> normalizeName(uiGame.getPlayerNameByInitialPosition(uiRound.discarderInitialSeat))
                }
            ).also { append(",") }
            append("${uiRound.handPoints},")
            append("${uiRound.pointsP1},")
            append("${uiRound.pointsP2},")
            append("${uiRound.pointsP3},")
            append("${uiRound.pointsP4},")
            append("${uiRound.penaltyP1},")
            append("${uiRound.penaltyP2},")
            append("${uiRound.penaltyP3},")
            append("${uiRound.penaltyP4},")
            append("${uiGame.gameName},")
            append("${uiGame.startDate},")
            append(uiGame.endDate)
            appendLine()
        }
    }
}