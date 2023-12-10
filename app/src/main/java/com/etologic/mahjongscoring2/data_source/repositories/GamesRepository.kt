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

import com.etologic.mahjongscoring2.data_source.model.DBGame
import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.GamesDao
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesRepository
@Inject constructor(
    private var gamesDao: GamesDao,
) {
    fun insertOne(dbGame: DBGame): Single<GameId> = gamesDao.insertOne(dbGame)

    fun updateOne(dbGame: DBGame): Single<Boolean> = gamesDao.updateOne(dbGame).map { it == 1 }

    fun deleteOne(gameId: GameId): Single<Boolean> = gamesDao.deleteOne(gameId).map { it == 1 }

    fun getAll(): Single<List<DBGame>> = gamesDao.getAll()

    fun getOne(gameId: GameId): Single<DBGame> = gamesDao.getOne(gameId)
}
