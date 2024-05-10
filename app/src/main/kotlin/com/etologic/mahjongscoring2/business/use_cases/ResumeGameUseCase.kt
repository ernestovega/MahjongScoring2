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

import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.business.model.entities.UiRound.Companion.NOT_SET_ROUND_ID
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import java.util.Date
import javax.inject.Inject

class ResumeGameUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiGame: UiGame, gameRoundsNumber: Int): Result<Boolean> =
        if (gameRoundsNumber < MAX_MCR_ROUNDS) {
            gamesRepository.updateOne(
                DbGame(
                    gameId = uiGame.gameId,
                    nameP1 = uiGame.nameP1,
                    nameP2 = uiGame.nameP2,
                    nameP3 = uiGame.nameP3,
                    nameP4 = uiGame.nameP4,
                    startDate = uiGame.startDate,
                    endDate = null,
                    gameName = uiGame.gameName,
                )
            )
            roundsRepository.insertOne(
                DbRound(
                    gameId = uiGame.gameId,
                    roundId = NOT_SET_ROUND_ID
                )
            )
        } else {
            Result.success(false)
        }
}
