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
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.business.model.exceptions.GamesNotFoundException
import com.etologic.mahjongscoring2.business.model.exceptions.RoundsNotFoundException
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import javax.inject.Inject

class ExportDbToCsvUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    @Throws(GameNotFoundException::class, RoundsNotFoundException::class)
    suspend operator fun invoke(getExternalFilesDir: () -> File?): Result<List<File>> =
        runCatching {
            val dbGames = gamesRepository.getAllFlow().firstOrNull() ?: throw GamesNotFoundException()
            val dbRounds = roundsRepository.getAllFlow().firstOrNull() ?: throw RoundsNotFoundException()

            val csvGames = convertDbGamesToCsv(dbGames)
            val csvRounds = convertRoundsToCsv(dbRounds)

            val externalFilesDir = getExternalFilesDir.invoke()
            val gamesCsvFile = writeToFile("Games", csvGames, externalFilesDir)
            val roundsCsvFile = writeToFile("Rounds", csvRounds, externalFilesDir)

            listOf(gamesCsvFile, roundsCsvFile)
        }

    private fun convertDbGamesToCsv(dbGames: List<DbGame>): String =
        with(StringBuilder()) {
            buildGamesHeader()
            buildGamesRows(dbGames)
            toString()
        }

    private fun StringBuilder.buildGamesHeader() {
        append("gameId,")
        append("gameName,")
        append("nameP1,")
        append("nameP2,")
        append("nameP3,")
        append("nameP4,")
        append("startDate,")
        append("endDate")
        appendLine()
    }

    private fun StringBuilder.buildGamesRows(dbGames: List<DbGame>) {
        dbGames.forEach { dbGame ->
            append("${dbGame.gameId},")
            append("${normalizeName(dbGame.gameName)},")
            append("${normalizeName(dbGame.nameP1)},")
            append("${normalizeName(dbGame.nameP2)},")
            append("${normalizeName(dbGame.nameP3)},")
            append("${normalizeName(dbGame.nameP4)},")
            append("${dbGame.startDate},")
            append("${dbGame.endDate}")
            appendLine()
        }
    }

    private fun convertRoundsToCsv(dbRounds: List<DbRound>): String =
        with(StringBuilder()) {
            buildRoundsHeader()
            buildRoundsRows(dbRounds)
            toString()
        }

    private fun StringBuilder.buildRoundsHeader() {
        append("gameId,")
        append("roundId,")
        append("winnerInitialSeat,")
        append("discarderInitialSeat,")
        append("handPoints,")
        append("penaltyP1,")
        append("penaltyP2,")
        append("penaltyP3,")
        append("penaltyP4")
        appendLine()
    }

    private fun StringBuilder.buildRoundsRows(dbRounds: List<DbRound>) {
        dbRounds.forEach { dbRound ->
            append("${dbRound.gameId},")
            append("${dbRound.roundId},")
            append("${dbRound.winnerInitialSeat?.code},")
            append("${dbRound.discarderInitialSeat?.code},")
            append("${dbRound.handPoints},")
            append("${dbRound.penaltyP1},")
            append("${dbRound.penaltyP2},")
            append("${dbRound.penaltyP3},")
            append("${dbRound.penaltyP4}")
            appendLine()
        }
    }
}
