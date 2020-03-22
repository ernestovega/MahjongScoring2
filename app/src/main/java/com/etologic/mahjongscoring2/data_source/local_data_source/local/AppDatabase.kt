package com.etologic.mahjongscoring2.data_source.local_data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.CombinationDescriptionTypeConverter
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.DateConverter
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.*
import com.etologic.mahjongscoring2.business.model.entities.Combination
import com.etologic.mahjongscoring2.business.model.entities.Game
import com.etologic.mahjongscoring2.business.model.entities.Round

@Database(entities = [Game::class, Round::class, Combination::class], version = AppDatabase.VERSION)
@TypeConverters(DateConverter::class, CombinationDescriptionTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    internal companion object {
        internal const val DB_NAME = "MahjongScoring2"
        internal const val VERSION = 1
    }
    
    internal abstract val combinationsDao: CombinationsDao
    internal abstract val gamesDao: GamesDao
    internal abstract val roundsDao: RoundsDao
    internal abstract val gameWithRoundsDao: GameWithRoundsDao
}