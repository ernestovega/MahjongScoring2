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
import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import io.reactivex.Single

@Dao
interface RoundsDao {

    @Query("SELECT * FROM Rounds")
    fun getAll(): Single<List<Round>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    fun insertOne(round: Round): Single<Long>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId")
    fun getGameRounds(gameId: GameId): Single<List<Round>>

    @Update
    fun updateOne(round: Round): Single<Int>

    @Query("DELETE FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    fun deleteOne(gameId: GameId, roundId: RoundId): Single<Int>

    @Query("DELETE FROM Rounds WHERE gameId = :gameId")
    fun deleteGameRounds(gameId: GameId): Single<Int>

    @Query("DELETE FROM Rounds")
    fun deleteAll(): Single<Int>
}
