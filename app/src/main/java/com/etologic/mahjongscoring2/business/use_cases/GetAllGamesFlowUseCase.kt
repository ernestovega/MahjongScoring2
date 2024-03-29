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

import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class GetAllGamesFlowUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    operator fun invoke(): Flow<List<UIGame>> =
        combine(
            gamesRepository.getAllFlow(),
            roundsRepository.getAllFlow(),
        ) { dbGames, rounds ->
            dbGames.map { game ->
                val gameRounds = rounds.filter { round -> round.gameId == game.gameId }
                if (gameRounds.isEmpty()) {
                    return@combine null
                } else {
                    UIGame(game, gameRounds)
                }
            }
        }.mapNotNull { it }

}
