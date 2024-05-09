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
package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.GameId
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.RoundId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoundsRepository @Inject constructor(private var roundsDao: RoundsDao) {

    fun getAllFlow(): Flow<List<DbRound>> = roundsDao.getAll()

    fun getAllByGame(gameId: GameId): Flow<List<DbRound>> = roundsDao.getGameRounds(gameId)

    suspend fun insertOne(round: DbRound): Result<Boolean> = runCatching { roundsDao.insertOne(round) > 0 }

    suspend fun updateOne(round: DbRound): Result<Boolean> = runCatching { roundsDao.updateOne(round) == 1 }

    suspend fun deleteByGame(gameId: GameId): Result<Boolean> = runCatching { roundsDao.deleteGameRounds(gameId) >= 0 }

    suspend fun deleteOne(gameId: GameId, roundId: RoundId): Result<Boolean> = runCatching {
        roundsDao.deleteOne(gameId, roundId) == 1
    }
}
