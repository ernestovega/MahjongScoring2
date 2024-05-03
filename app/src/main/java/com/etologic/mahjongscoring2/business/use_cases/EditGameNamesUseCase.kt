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
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import javax.inject.Inject

class EditGameNamesUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
) {
    suspend operator fun invoke(
        dbGame: DBGame,
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
    ): Result<Boolean> {
        dbGame.updateNames(
            gameName = normalizeName(gameName),
            nameP1 = normalizeName(nameP1),
            nameP2 = normalizeName(nameP2),
            nameP3 = normalizeName(nameP3),
            nameP4 = normalizeName(nameP4),
        )
        return gamesRepository.updateOne(dbGame)
    }

    private fun DBGame.updateNames(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String
    ) {
        this.gameName = gameName
        this.nameP1 = nameP1
        this.nameP2 = nameP2
        this.nameP3 = nameP3
        this.nameP4 = nameP4
    }
}
