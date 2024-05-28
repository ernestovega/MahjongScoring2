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
import com.etologic.mahjongscoring2.business.model.dtos.PortableGame
import com.etologic.mahjongscoring2.business.model.dtos.toDbGame
import com.etologic.mahjongscoring2.business.model.dtos.toDbRounds
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.exceptions.JsonGamesNotValidException
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.repositories.games.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.RoundsRepository
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ImportGamesUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
    private val deleteGameUseCase: DeleteGameUseCase,
) {
    suspend operator fun invoke(uri: Uri, getContentResolver: () -> ContentResolver): Result<List<GameId>> =
        runCatching {
            val jsonText = uri.readText(getContentResolver) ?: throw JsonGamesNotValidException()
            val portableGames = Json.decodeFromString(ListSerializer(PortableGame.serializer()), jsonText)
            portableGames.map { portableGame ->
                val gameId = gamesRepository.insertOne(DbGame()).getOrThrow()
                val dbGame = portableGame.toDbGame(gameId)
                gamesRepository.updateOne(dbGame)
                    .onFailure { deleteGameUseCase(gameId) }
                val importedDbRounds = portableGame.rounds.toDbRounds(gameId)
                importedDbRounds.forEach { importedDbRound ->
                    roundsRepository.insertOne(importedDbRound)
                        .onFailure { deleteGameUseCase(gameId) }
                }
                gameId
            }
        }

    private fun Uri.readText(getContentResolver: () -> ContentResolver): String? =
        getContentResolver().openInputStream(this)?.bufferedReader()?.use { it.readText() }
}