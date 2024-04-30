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

import com.etologic.mahjongscoring2.business.model.dtos.ExportedDb
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.model.DBGame
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import javax.inject.Inject

class ExportDbToCsvUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(externalFilesDir: File?): ExportedDb {
        val csvGames = convertDbGamesToCsv(gamesRepository.getAllFlow().firstOrNull().orEmpty())
        val csvRounds = convertRoundsToCsv(roundsRepository.getAllFlow().firstOrNull().orEmpty())
        val gamesCsvFile = writeToFile("Games", csvGames, externalFilesDir)
        val roundsCsvFile = writeToFile("Rounds", csvRounds, externalFilesDir)
        return ExportedDb(gamesCsvFile, roundsCsvFile)
    }

    private fun convertDbGamesToCsv(dbGames: List<DBGame>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine(
            "gameId,gameName," +
                    "nameP1,nameP2,nameP3,nameP4," +
                    "startDate,endDate"
        )
        dbGames.forEach { dbGame ->
            stringBuilder.appendLine(
                "${dbGame.gameId}," +
                        "\"${normalizeName(dbGame.gameName)}\"," +
                        "\"${normalizeName(dbGame.nameP1)}\"," +
                        "\"${normalizeName(dbGame.nameP2)}\"," +
                        "\"${normalizeName(dbGame.nameP3)}\"," +
                        "\"${normalizeName(dbGame.nameP4)}\"," +
                        "${dbGame.startDate},${dbGame.endDate}"
            )
        }
        return stringBuilder.toString()
    }

    private fun convertRoundsToCsv(dbRounds: List<Round>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine(
            "gameId,roundId," +
                    "winnerInitialSeat,discarderInitialSeat,handPoints," +
                    "pointsP1,pointsP2,pointsP3,pointsP4," +
                    "penaltyP1,penaltyP2,penaltyP3,penaltyP4"
        )
        dbRounds.forEach { round ->
            stringBuilder.appendLine(
                "${round.gameId},${round.roundId}," +
                        "${round.winnerInitialSeat},${round.discarderInitialSeat},${round.handPoints}," +
                        "${round.pointsP1},${round.pointsP2},${round.pointsP3},${round.pointsP4}," +
                        "${round.penaltyP1},${round.penaltyP2},${round.penaltyP3},${round.penaltyP4}"
            )
        }
        return stringBuilder.toString()
    }

    private fun writeToFile(tableName: String, csvData: String, externalFilesDir: File?): File {
        val fileName = "$tableName.csv"
        val file = File(externalFilesDir, fileName)
        file.writeText(csvData)
        return file
    }
}
