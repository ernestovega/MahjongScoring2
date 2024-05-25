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
package com.etologic.mahjongscoring2.data_source.repositories.rounds

import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRoundsRepository @Inject constructor(
    private var roundsDao: RoundsDao
) : RoundsRepository {

    override fun getAllFlow(): Flow<List<DbRound>> = roundsDao.getAllFlow()

    override fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>> = roundsDao.getGameRoundsFlow(gameId)

    override suspend fun getGameRounds(gameId: GameId): Result<List<DbRound>> = runCatching { roundsDao.getGameRounds(gameId) }

    override suspend fun getOne(gameId: GameId, roundId: RoundId): Result<DbRound> = runCatching { roundsDao.getOne(gameId, roundId) }

    override suspend fun insertOne(dbRound: DbRound): Result<Boolean> = runCatching { roundsDao.insertOne(dbRound) > 0 }

    override suspend fun updateOne(dbRound: DbRound): Result<Boolean> = runCatching { roundsDao.updateOne(dbRound) == 1 }

    override suspend fun deleteGameRounds(gameId: GameId): Result<Boolean> = runCatching { roundsDao.deleteGameRounds(gameId) >= 0 }

    override suspend fun deleteOne(gameId: GameId, roundId: RoundId): Result<Boolean> = runCatching { roundsDao.deleteOne(gameId, roundId) == 1 }
}
