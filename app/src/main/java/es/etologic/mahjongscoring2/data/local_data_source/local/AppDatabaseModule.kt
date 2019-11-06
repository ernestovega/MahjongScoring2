package es.etologic.mahjongscoring2.data.local_data_source.local

import androidx.room.Room
import dagger.Module
import dagger.Provides
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.*
import es.etologic.mahjongscoring2.injection.MahjongScoringApp
import javax.inject.Singleton

@Module
internal class AppDataBaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(mahjongScoringApp: MahjongScoringApp): AppDatabase {
        return Room
            .databaseBuilder(mahjongScoringApp, AppDatabase::class.java, AppDatabase.DB_NAME)
            .build()
    }
    
    @Provides
    fun providePlayersDao(dataBase: AppDatabase): PlayersDao {
        return dataBase.playersDao
    }
    
    @Provides
    fun provideCombinationsDao(dataBase: AppDatabase): CombinationsDao {
        return dataBase.combinationsDao
    }
    
    @Provides
    fun provideUserGameWithRoundsDao(dataBase: AppDatabase): GameWithRoundsDao {
        return dataBase.gameWithRoundsDao
    }
    
    @Provides
    fun provideUserGamesDao(dataBase: AppDatabase): GamesDao {
        return dataBase.gamesDao
    }
    
    @Provides
    fun provideUserRoundsDao(dataBase: AppDatabase): RoundsDao {
        return dataBase.roundsDao
    }
}