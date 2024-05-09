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
import com.etologic.mahjongscoring2.app.utils.writeToFile
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.data_source.model.GameId
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import java.lang.String.format
import java.util.Locale
import javax.inject.Inject

class ExportGameToCsvUseCase @Inject constructor(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase
) {
    @Throws(GameNotFoundException::class)
    suspend operator fun invoke(
        gameId: GameId,
        getExternalFilesDir: () -> File?,
        getStringRes: (Int) -> String,
    ): Result<List<File>> = runCatching {
        val uiGame = getOneGameFlowUseCase.invoke(gameId).firstOrNull() ?: throw GameNotFoundException(getStringRes(R.string.ups_something_wrong))
        val csvText = buildCsvText(uiGame, getStringRes)
        val csvFile = writeToFile(
            name = "${getStringRes(R.string.game)}_${normalizeName(uiGame.dbGame.gameName).replace(" ", "-")}",
            csvText = csvText,
            externalFilesDir = getExternalFilesDir.invoke(),
        )
        listOf(csvFile)
    }

    private fun buildCsvText(uiGame: UiGame, getStrRes: (Int) -> String): String =
        with(StringBuilder()) {
            buildHeader(uiGame, getStrRes)
            buildRows(uiGame)
            toString()
        }

    private fun StringBuilder.buildHeader(uiGame: UiGame, getStrRes: (Int) -> String) {
        append("${getStrRes(R.string.round)},")
        append("${getStrRes(R.string.winner_).replace(":", "")},")
        append("${getStrRes(R.string.discarder_)},")
        append("${getStrRes(R.string.points_).replace(":", "")} ${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(TableWinds.EAST))},")
        append("${getStrRes(R.string.points_).replace(":", "")} ${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(TableWinds.SOUTH))},")
        append("${getStrRes(R.string.points_).replace(":", "")} ${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(TableWinds.WEST))},")
        append("${getStrRes(R.string.points_).replace(":", "")} ${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(TableWinds.NORTH))},")
        append("${getStrRes(R.string.penalty)} ${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(TableWinds.EAST))},")
        append("${getStrRes(R.string.penalty)} ${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(TableWinds.SOUTH))},")
        append("${getStrRes(R.string.penalty)} ${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(TableWinds.WEST))},")
        append("${getStrRes(R.string.penalty)} ${normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(TableWinds.NORTH))}")
        appendLine()
    }

    private fun StringBuilder.buildRows(uiGame: UiGame) {
        uiGame.uiRounds.forEach { uiRound ->
            append("${uiRound.roundNumber},")
            append(
                when (uiRound.dbRound.winnerInitialSeat) {
                    null,
                    NONE -> "-"

                    else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(uiRound.dbRound.winnerInitialSeat!!))
                }
            ).also { append(",") }
            append(
                when (uiRound.dbRound.discarderInitialSeat) {
                    null,
                    NONE -> "-"

                    else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(uiRound.dbRound.discarderInitialSeat!!))
                }
            ).also { append(",") }
            append("${uiRound.pointsP1.toSignedString()},")
            append("${uiRound.pointsP2.toSignedString()},")
            append("${uiRound.pointsP3.toSignedString()},")
            append("${uiRound.pointsP4.toSignedString()},")
            append("${uiRound.dbRound.penaltyP1.toSignedString()},")
            append("${uiRound.dbRound.penaltyP2.toSignedString()},")
            append("${uiRound.dbRound.penaltyP3.toSignedString()},")
            append(uiRound.dbRound.penaltyP4.toSignedString())
            appendLine()
        }
    }

    private fun Int.toSignedString(): String = format(Locale.getDefault(), "%+d", this)
}