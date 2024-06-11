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
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesDao {

    @Query("SELECT * from Games ORDER BY startDate DESC, gameId DESC")
    fun getAllFlow(): Flow<List<DbGame>>

    @Query("SELECT * from Games WHERE gameId = :gameId")
    fun getOneFlow(gameId: GameId): Flow<DbGame>

    @Query("SELECT * from Games WHERE gameId = :gameId")
    suspend fun getOne(gameId: GameId): DbGame

    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    suspend fun insertOne(dbGame: DbGame): GameId

    @Update
    suspend fun updateOne(dbGame: DbGame): Int

    @Query("DELETE FROM Games WHERE gameId = :gameId")
    suspend fun deleteOne(gameId: GameId): Int
}
