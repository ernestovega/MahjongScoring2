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

import com.etologic.mahjongscoring2.data_source.model.DbGame
import com.etologic.mahjongscoring2.data_source.model.DbRound
import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import java.util.Calendar
import javax.inject.Inject

class CreateGameUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository
) {
    suspend operator fun invoke(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
        enableDiffCalcs: Boolean,
    ): Result<GameId> =
        gamesRepository.insertOne(
            DbGame(
                gameId = DbGame.NOT_SET_GAME_ID,
                gameName = gameName,
                nameP1 = nameP1,
                nameP2 = nameP2,
                nameP3 = nameP3,
                nameP4 = nameP4,
                startDate = Calendar.getInstance().time,
                endDate = null,
                areDiffCalcsEnabled = enableDiffCalcs,
            )
        )
            .onSuccess { gameId -> roundsRepository.insertOne(DbRound(gameId)) }
}
