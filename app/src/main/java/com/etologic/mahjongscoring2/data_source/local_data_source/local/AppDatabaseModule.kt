package com.etologic.mahjongscoring2.data_source.local_data_source.local

import androidx.room.Room
import dagger.Module
import dagger.Provides
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.*
import com.etologic.mahjongscoring2.injection.MahjongScoringApp
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
    internal fun provideGameWithRoundsDao(dataBase: AppDatabase): GameWithRoundsDao {
        return dataBase.gameWithRoundsDao
    }
}