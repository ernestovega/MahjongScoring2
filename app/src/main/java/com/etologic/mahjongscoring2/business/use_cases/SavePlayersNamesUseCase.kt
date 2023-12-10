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

import com.etologic.mahjongscoring2.data_source.model.DBGame
import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import io.reactivex.Single
import javax.inject.Inject

class SavePlayersNamesUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val getGameUseCase: GetGameUseCase,
) {
    operator fun invoke(gameId: GameId, names: Array<String>): Single<UIGame> =
        gamesRepository.getOne(gameId)
            .flatMap { dbGame ->
                dbGame.updateNames(names)
                gamesRepository.updateOne(dbGame)
                    .flatMap { getGameUseCase(gameId) }
            }

    private fun DBGame.updateNames(names: Array<String>) {
        this.nameP1 = names[0]
        this.nameP2 = names[1]
        this.nameP3 = names[2]
        this.nameP4 = names[3]
    }
}
