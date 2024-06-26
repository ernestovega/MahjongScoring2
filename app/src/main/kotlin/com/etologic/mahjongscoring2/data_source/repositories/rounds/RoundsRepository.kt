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

package com.etologic.mahjongscoring2.data_source.repositories.rounds

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import kotlinx.coroutines.flow.Flow

interface RoundsRepository {
    fun getAllFlow(): Flow<List<DbRound>>
    fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>>
    suspend fun getGameRounds(gameId: GameId): Result<List<DbRound>>
    suspend fun getOne(roundId: RoundId): Result<DbRound>
    suspend fun insertOne(dbRound: DbRound): Result<Boolean>
    suspend fun updateOne(dbRound: DbRound): Result<Boolean>
    suspend fun deleteGameRounds(gameId: GameId): Result<Boolean>
    suspend fun deleteOne(roundId: RoundId): Result<Boolean>
}