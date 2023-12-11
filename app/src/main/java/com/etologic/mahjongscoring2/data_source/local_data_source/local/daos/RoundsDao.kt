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
package com.etologic.mahjongscoring2.data_source.local_data_source.local.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.data_source.model.GameId
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundsDao {

    @Query("SELECT * FROM Rounds")
    fun getAll(): Flow<List<Round>>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId")
    fun getGameRounds(gameId: GameId): Flow<List<Round>>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    fun getOne(gameId: GameId, roundId: RoundId): Round

    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    suspend fun insertOne(round: Round): RoundId

    @Update
    suspend fun updateOne(round: Round): Int

    @Query("DELETE FROM Rounds WHERE gameId = :gameId")
    suspend fun deleteGameRounds(gameId: GameId): Int

    @Query("DELETE FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    suspend fun deleteOne(gameId: GameId, roundId: RoundId): Int
}
