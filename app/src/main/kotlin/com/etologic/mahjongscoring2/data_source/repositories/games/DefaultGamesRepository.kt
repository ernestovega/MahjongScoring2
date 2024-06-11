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
package com.etologic.mahjongscoring2.data_source.repositories.games

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultGamesRepository @Inject constructor(
    private var gamesDao: GamesDao,
) : GamesRepository {
    override fun getAllFlow(): Flow<List<DbGame>> = gamesDao.getAllFlow()

    override fun getOneFlow(gameId: GameId): Flow<DbGame> = gamesDao.getOneFlow(gameId)

    override suspend fun getOne(gameId: GameId): Result<DbGame> = runCatching { gamesDao.getOne(gameId) }

    override suspend fun insertOne(dbGame: DbGame): Result<GameId> = runCatching { gamesDao.insertOne(dbGame) }

    override suspend fun updateOne(dbGame: DbGame): Result<Boolean> = runCatching { gamesDao.updateOne(dbGame) == 1 }

    override suspend fun deleteOne(gameId: GameId): Result<Boolean> = runCatching { gamesDao.deleteOne(gameId) == 1 }
}
