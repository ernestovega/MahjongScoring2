package es.etologic.mahjongscoring2.data.local_data_source.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import es.etologic.mahjongscoring2.domain.model.GameWithRounds

@Dao
interface GameWithRoundsDao {
    
    @Transaction
    @Query("SELECT * from Games")
    fun getAllGamesWithRounds(): List<GameWithRounds>
    
    @Transaction
    @Query("SELECT * from Games WHERE gameId = :gameId ORDER BY startDate")
    fun getGameWithRoundsOrderedByDateDesc(gameId: Long): GameWithRounds
}
