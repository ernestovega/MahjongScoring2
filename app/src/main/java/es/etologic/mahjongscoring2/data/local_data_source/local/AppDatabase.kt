package es.etologic.mahjongscoring2.data.local_data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.etologic.mahjongscoring2.data.local_data_source.local.converters.CombinationDescriptionTypeConverter
import es.etologic.mahjongscoring2.data.local_data_source.local.converters.DateConverter
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.*
import es.etologic.mahjongscoring2.domain.model.Combination
import es.etologic.mahjongscoring2.domain.model.Game
import es.etologic.mahjongscoring2.domain.model.Player
import es.etologic.mahjongscoring2.domain.model.Round

@Database(entities = [Player::class, Game::class, Round::class, Combination::class], version = AppDatabase.VERSION)
@TypeConverters(DateConverter::class, CombinationDescriptionTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    companion object {
        const val DB_NAME = "MahjongScoring2"
        const val VERSION = 1
    }
    
    abstract val combinationsDao: CombinationsDao
    abstract val playersDao: PlayersDao
    abstract val gamesDao: GamesDao
    abstract val roundsDao: RoundsDao
    abstract val gameWithRoundsDao: GameWithRoundsDao
}