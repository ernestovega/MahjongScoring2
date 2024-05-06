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
package com.etologic.mahjongscoring2.data_source.local_data_source

import android.content.Context
import androidx.room.Room
import com.etologic.mahjongscoring2.data_source.local_data_source.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_source.daos.RoundsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppDataBaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "MahjongScoring2")
            .addMigrations(Migration1to2)
            .build()

    @Provides
    fun provideGamesDao(dataBase: AppDatabase): GamesDao = dataBase.gamesDao

    @Provides
    fun provideRoundsDao(dataBase: AppDatabase): RoundsDao = dataBase.roundsDao
}