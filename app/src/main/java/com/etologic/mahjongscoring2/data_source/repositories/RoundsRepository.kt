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

import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.RoundsDao
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoundsRepository @Inject constructor(private var roundsDao: RoundsDao) {

    fun insertOne(round: Round): Single<Long> = roundsDao.insertOne(round)

    fun updateOne(round: Round): Single<Boolean> = roundsDao.updateOne(round).map { it == 1 }

    fun deleteOne(gameId: GameId, roundId: RoundId): Single<Boolean> = roundsDao.deleteOne(gameId, roundId).map { it == 1 }

    fun deleteByGame(gameId: GameId): Single<Boolean> = roundsDao.deleteGameRounds(gameId).map { it >= 0 }

    fun getAll(): Single<List<Round>> = roundsDao.getAll()

    fun getAllByGame(gameId: GameId): Single<List<Round>> = roundsDao.getGameRounds(gameId)
}
