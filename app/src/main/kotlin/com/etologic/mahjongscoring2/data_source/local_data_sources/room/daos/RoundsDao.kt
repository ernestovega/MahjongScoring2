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
package com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundsDao {

    @Query("SELECT * FROM Rounds")
    fun getAllFlow(): Flow<List<DbRound>>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId")
    fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId ORDER BY roundId ASC")
    suspend fun getGameRounds(gameId: GameId): List<DbRound>

    @Query("SELECT * FROM Rounds WHERE roundId = :roundId")
    suspend fun getOne(roundId: RoundId): DbRound

    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    suspend fun insertOne(round: DbRound): RoundId

    @Update
    suspend fun updateOne(round: DbRound): Int

    @Query("DELETE FROM Rounds WHERE gameId = :gameId")
    suspend fun deleteGameRounds(gameId: GameId): Int

    @Query("DELETE FROM Rounds WHERE roundId = :roundId")
    suspend fun deleteOne(roundId: RoundId): Int
}
