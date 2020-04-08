/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.data_source.local_data_source.local

import androidx.room.Room
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.CombinationsDao
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.TableDao
import com.etologic.mahjongscoring2.injection.MahjongScoringApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class AppDataBaseModule {
    
    @Provides
    @Singleton
    internal fun provideAppDatabase(mahjongScoringApp: MahjongScoringApp): AppDatabase {
        return Room
            .databaseBuilder(mahjongScoringApp, AppDatabase::class.java, AppDatabase.DB_NAME)
            .build()
    }
    
    @Provides
    internal fun provideCombinationsDao(dataBase: AppDatabase): CombinationsDao {
        return dataBase.combinationsDao
    }
    
    @Provides
    internal fun provideGamesDao(dataBase: AppDatabase): GamesDao {
        return dataBase.gamesDao
    }
    
    @Provides
    internal fun provideRoundsDao(dataBase: AppDatabase): RoundsDao {
        return dataBase.roundsDao
    }
    
    @Provides
    internal fun provideGameWithRoundsDao(dataBase: AppDatabase): TableDao {
        return dataBase.tableDao
    }
}