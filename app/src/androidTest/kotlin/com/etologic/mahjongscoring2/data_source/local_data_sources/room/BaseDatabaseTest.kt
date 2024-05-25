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

package com.etologic.mahjongscoring2.data_source.local_data_sources.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.repositories.games.DefaultGamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.DefaultRoundsRepository

abstract class BaseDatabaseTest {

    private lateinit var database: AppDatabase
    protected lateinit var gamesDao: GamesDao
    protected lateinit var roundsDao: RoundsDao
    protected lateinit var gamesRepository: DefaultGamesRepository
    protected lateinit var roundsRepository: DefaultRoundsRepository

    protected fun initDataBaseAndRepos() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        gamesDao = database.gamesDao
        roundsDao = database.roundsDao
        gamesRepository = DefaultGamesRepository(gamesDao)
        roundsRepository = DefaultRoundsRepository(roundsDao)
    }

    fun closeDatabase() {
        database.close()
    }
}