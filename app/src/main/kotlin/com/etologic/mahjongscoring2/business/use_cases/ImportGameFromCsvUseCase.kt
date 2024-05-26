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

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.VisibleForTesting
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.exceptions.CsvGameNotValidException
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.games.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.RoundsRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ImportGameFromCsvUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
    private val deleteGameUseCase: DeleteGameUseCase,
) {
    suspend operator fun invoke(uri: Uri, getContentResolver: () -> ContentResolver): Result<GameId> =
        runCatching {
            val gameId = gamesRepository.insertOne(DbGame()).getOrThrow()
            val csvText = uri.readText(getContentResolver) ?: throw CsvGameNotValidException()
            val csvGame = toCsvGame(csvText)
            val dbGame = csvGame.toDbGame(gameId)
            gamesRepository.updateOne(dbGame)
                .onFailure { deleteGameUseCase(gameId) }
            val importedDbRounds = csvGame.rounds.toDbRounds(gameId)
            importedDbRounds.forEach { importedDbRound ->
                roundsRepository.insertOne(importedDbRound)
                    .onFailure { deleteGameUseCase(gameId) }
            }
            gameId
        }


    private fun Uri.readText(getContentResolver: () -> ContentResolver): String? =
        getContentResolver().openInputStream(this)?.bufferedReader()?.use { it.readText() }

    private fun CsvGame.toDbGame(gameId: GameId): DbGame =
        DbGame(
            gameId = gameId,
            nameP1 = nameP1,
            nameP2 = nameP2,
            nameP3 = nameP3,
            nameP4 = nameP4,
            startDate = startDate.toDate(),
            endDate = endDate.toDate(),
            gameName = gameName,
        )

    private fun String.toDate(): Date =
        SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).let { dateFormat ->
            try {
                dateFormat.parse(this) ?: Date()
            } catch (e: ParseException) {
                Date()
            }
        }

    @VisibleForTesting
    fun toCsvGame(csvText: String): CsvGame {
        val lines = csvText.lines().filter { it.isNotBlank() }
        val header = lines.first().split(",")
        val firstRecord = lines.first().split(",")
        val csvGame = CsvGame(
            gameName = firstRecord[12],
            startDate = firstRecord[13],
            endDate = firstRecord[14],
            nameP1 = header[4].getPlayerNameFromPointsHeader(),
            nameP2 = header[5].getPlayerNameFromPointsHeader(),
            nameP3 = header[6].getPlayerNameFromPointsHeader(),
            nameP4 = header[7].getPlayerNameFromPointsHeader(),
        )

        val csvRounds = mutableListOf<CsvRound>()
        for (line in lines.drop(1)) {
            val values = line.split(",")
            val record = CsvRound(
                roundNumber = values[0].toInt(),
                winnerInitialSeat = values[1],
                discarderInitialSeat = values[2],
                handPoints = values[3].toInt(),
                pointsP1 = values[4].toInt(),
                pointsP2 = values[5].toInt(),
                pointsP3 = values[6].toInt(),
                pointsP4 = values[7].toInt(),
                penaltyP1 = values[8].toInt(),
                penaltyP2 = values[9].toInt(),
                penaltyP3 = values[10].toInt(),
                penaltyP4 = values[11].toInt(),
//                gameName = values[12],
//                startDate = values[13],
//                endDate = values[14]
            )
            csvRounds.add(record)
        }

        return csvGame.copy(rounds = csvRounds)
    }

    private fun String.getPlayerNameFromPointsHeader() = replace("Points ", "")

    private fun List<CsvRound>.toDbRounds(gameId: GameId): List<DbRound> =
        this.map { csvRound ->
            with(csvRound) {
                DbRound(
                    gameId = gameId,
                    winnerInitialSeat = TableWinds.from(winnerInitialSeat),
                    discarderInitialSeat = TableWinds.from(discarderInitialSeat),
                    handPoints = handPoints,
                    penaltyP1 = penaltyP1,
                    penaltyP2 = penaltyP2,
                    penaltyP3 = penaltyP3,
                    penaltyP4 = penaltyP4,
                )
            }
        }

    @VisibleForTesting
    data class CsvGame(
        val gameName: String,
        val startDate: String,
        val endDate: String,
        val nameP1: String,
        val nameP2: String,
        val nameP3: String,
        val nameP4: String,
        val rounds: List<CsvRound> = emptyList(),
    )

    @VisibleForTesting
    data class CsvRound(
        val roundNumber: Int,
        val winnerInitialSeat: String,
        val discarderInitialSeat: String,
        val handPoints: Int,
        val pointsP1: Int,
        val pointsP2: Int,
        val pointsP3: Int,
        val pointsP4: Int,
        val penaltyP1: Int,
        val penaltyP2: Int,
        val penaltyP3: Int,
        val penaltyP4: Int,
    )
}