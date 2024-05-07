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
import com.etologic.mahjongscoring2.data_source.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import javax.inject.Inject

class EndRoundUseCase @Inject constructor(
    private val roundsRepository: RoundsRepository,
    private val endGameUseCase: EndGameUseCase,
) {
    suspend operator fun invoke(uiGame: UiGame): Result<Boolean> =
        if (uiGame.uiRounds.size < UiGame.MAX_MCR_ROUNDS) {
            roundsRepository.insertOne(DbRound(uiGame.dbGame.gameId))
        } else {
            endGameUseCase(uiGame)
        }
}
