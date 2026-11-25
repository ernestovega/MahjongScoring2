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

package com.etologic.mahjongscoring2.injection

import android.content.Context
import androidx.room.Room
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDataBaseModule
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.RoundsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Replaces the real database with an in-memory database in Hilt tests. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AppDataBaseModule::class])
class TestAppDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()

    @Provides
    fun provideGamesDao(dataBase: AppDatabase): GamesDao = dataBase.gamesDao

    @Provides
    fun provideRoundsDao(dataBase: AppDatabase): RoundsDao = dataBase.roundsDao
}
