/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.business.use_cases.current_game

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.repositories.CurrentTableRepository
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import io.reactivex.Single
import javax.inject.Inject

class SaveCurrentPlayersUseCase @Inject
constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val gamesRepository: GamesRepository
) {

    internal fun saveCurrentGamePlayersNames(names: Array<String>): Single<Table> =
        currentTableRepository.get()
            .flatMap { currentGameWithRounds ->
                currentGameWithRounds.game.nameP1 = names[0]
                currentGameWithRounds.game.nameP2 = names[1]
                currentGameWithRounds.game.nameP3 = names[2]
                currentGameWithRounds.game.nameP4 = names[3]

                gamesRepository.updateOne(currentGameWithRounds.game)
                    .flatMap { currentTableRepository.set(currentGameWithRounds) }
                    .map { currentGameWithRounds }
            }
}
