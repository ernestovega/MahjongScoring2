/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
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

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.business.model.exceptions.RoundsNotFoundException
import com.etologic.mahjongscoring2.business.use_cases.mappers.toUiGame
import com.etologic.mahjongscoring2.data_source.repositories.games.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.RoundsRepository
import javax.inject.Inject

class GetOneGameUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(gameId: GameId): Result<UiGame> =
        runCatching {
            val dbGame = gamesRepository.getOne(gameId).getOrNull() ?: throw GameNotFoundException()
            val dbGameRounds = roundsRepository.getGameRounds(gameId).getOrNull() ?: throw RoundsNotFoundException()
            dbGame.toUiGame(dbGameRounds)
        }
}
