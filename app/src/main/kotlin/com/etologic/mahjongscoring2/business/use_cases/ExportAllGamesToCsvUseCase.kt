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
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.business.model.exceptions.GamesNotFoundException
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import java.lang.String.format
import java.util.Locale
import javax.inject.Inject

class ExportAllGamesToCsvUseCase @Inject constructor(
    private val getAllGamesFlowUseCase: GetAllGamesFlowUseCase
) {
    @Throws(GameNotFoundException::class)
    suspend operator fun invoke(
        getExternalFilesDir: () -> File?,
        getStringRes: (Int) -> String,
    ): Result<List<File>> = runCatching {
        val uiGames = getAllGamesFlowUseCase.invoke().firstOrNull() ?: throw GamesNotFoundException(getStringRes(R.string.ups_something_wrong))
        val csvText = buildCsvText(uiGames, getStringRes)
        val csvFile = writeToFile(
            name = getStringRes(R.string.games),
            csvText = csvText,
            externalFilesDir = getExternalFilesDir.invoke(),
        )
        listOf(csvFile)
    }

    private fun buildCsvText(uiGames: List<UiGame>, getStrRes: (Int) -> String): String =
        with(StringBuilder()) {
            buildHeader(getStrRes)
            buildRows(uiGames)
            toString()
        }

    private fun StringBuilder.buildHeader(getStrRes: (Int) -> String) {
        val pointsWord = getStrRes(R.string.points_).replace(":", "")
        val player1Abbr = "${getStrRes(R.string.player_abbr)}1"
        val player2Abbr = "${getStrRes(R.string.player_abbr)}2"
        val player3Abbr = "${getStrRes(R.string.player_abbr)}3"
        val player4Abbr = "${getStrRes(R.string.player_abbr)}4"
        append("${getStrRes(R.string.game_name)},")
        append("${getStrRes(R.string.round)},")
        append("${getStrRes(R.string.winner_).replace(":", "")},")
        append("${getStrRes(R.string.discarder_)},")
        append("${getStrRes(R.string.name)}_${player1Abbr},")
        append("${getStrRes(R.string.name)}_${player2Abbr},")
        append("${getStrRes(R.string.name)}_${player3Abbr},")
        append("${getStrRes(R.string.name)}_${player4Abbr},")
        append("${pointsWord}_${player1Abbr},")
        append("${pointsWord}_${player2Abbr},")
        append("${pointsWord}_${player3Abbr},")
        append("${pointsWord}_${player4Abbr},")
        append("${getStrRes(R.string.penalty)}_${player1Abbr},")
        append("${getStrRes(R.string.penalty)}_${player2Abbr},")
        append("${getStrRes(R.string.penalty)}_${player3Abbr},")
        append("${getStrRes(R.string.penalty)}_${player4Abbr}")
        appendLine()
    }

    private fun StringBuilder.buildRows(uiGames: List<UiGame>) {
        uiGames.forEach { uiGame ->
            val nameP1 = normalizeName(uiGame.dbGame.nameP1)
            val nameP2 = normalizeName(uiGame.dbGame.nameP2)
            val nameP3 = normalizeName(uiGame.dbGame.nameP3)
            val nameP4 = normalizeName(uiGame.dbGame.nameP4)
            uiGame.uiRounds.forEach { uiRound ->
                /*GameName*/append("${uiGame.dbGame.gameName},")
                /*Round*/append("${uiRound.roundNumber},")
                /*Winner*/append(
                    when (uiRound.dbRound.winnerInitialSeat) {
                        null,
                        NONE -> "-"

                        else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(uiRound.dbRound.winnerInitialSeat!!))
                    }
                ).also { append(",") }
                /*Discarder*/append(
                    when (uiRound.dbRound.discarderInitialSeat) {
                        null,
                        NONE -> "-"

                        else -> normalizeName(uiGame.dbGame.getPlayerNameByInitialPosition(uiRound.dbRound.discarderInitialSeat!!))
                    }
                ).also { append(",") }
                /*NameP1*/append("${nameP1},")
                /*NameP2*/append("${nameP2},")
                /*NameP3*/append("${nameP3},")
                /*NameP4*/append("${nameP4},")
                /*PointsP1*/append("${uiRound.pointsP1.toSignedString()},")
                /*PointsP2*/append("${uiRound.pointsP2.toSignedString()},")
                /*PointsP3*/append("${uiRound.pointsP3.toSignedString()},")
                /*PointsP4*/append("${uiRound.pointsP4.toSignedString()},")
                /*PenaltyP1*/append("${uiRound.dbRound.penaltyP1.toSignedString()},")
                /*PenaltyP2*/append("${uiRound.dbRound.penaltyP2.toSignedString()},")
                /*PenaltyP3*/append("${uiRound.dbRound.penaltyP3.toSignedString()},")
                /*PenaltyP4*/append(uiRound.dbRound.penaltyP4.toSignedString())
                appendLine()
            }
        }
    }

    private fun Int.toSignedString(): String = format(Locale.getDefault(), "%+d", this)
}