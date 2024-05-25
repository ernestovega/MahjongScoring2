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

package com.etologic.mahjongscoring2.data_source.repositories.games

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import kotlinx.coroutines.flow.Flow

interface GamesRepository {
    fun getAllFlow(): Flow<List<DbGame>>
    fun getOneFlow(gameId: GameId): Flow<DbGame>
    suspend fun getOne(gameId: GameId): Result<DbGame>
    suspend fun insertOne(dbGame: DbGame): Result<GameId>
    suspend fun updateOne(dbGame: DbGame): Result<Boolean>
    suspend fun deleteOne(gameId: GameId): Result<Boolean>
}